package com.rocgit.testing;

import com.rocgit.knife4j.common.*;
import com.rocgit.knife4j.config.*;
import com.rocgit.knife4j.dto.task.*;
import com.rocgit.knife4j.service.*;
import com.rocgit.knife4j.mapper.*;
import com.rocgit.knife4j.model.Task;
import com.rocgit.knife4j.model.UserSubscription;
import com.rocgit.knife4j.model.Project;
import com.rocgit.knife4j.model.Share;
import com.rocgit.knife4j.model.File;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Validation;
import javax.validation.Validator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 实际 Java 类的分支测试。数据库与通知为 Mockito 测试替身，不声称此处覆盖真实数据库。 */
public class CoreWhiteboxTest {
    private String sanitize(String filename) {
        return ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize",
                new MockMultipartFile("file", filename, "text/plain", new byte[]{1}));
    }
    @Test void j01() { assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize", (MultipartFile)null)); }
    @Test void j02() { assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize", new MockMultipartFile("file", "a.txt", "text/plain", new byte[0]))); }
    @Test void j03() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(50L*1024*1024);
        when(file.getOriginalFilename()).thenReturn("a.pdf");
        assertEquals("a.pdf", ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize", file));
    }
    @Test void j04() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(50L*1024*1024+1);
        assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize", file));
    }
    @Test void j05() { assertEquals("report.txt", sanitize("../../report.txt")); }
    @Test void j06() { assertEquals("report.txt", sanitize("C:\\temp\\report.txt")); }
    @Test void j07() { assertEquals("report.txt", sanitize("\t report\n.txt \r")); }
    @Test void j08() { assertEquals("REPORT.PDF", sanitize("REPORT.PDF")); }
    @Test void j09() {
        String longName = String.join("", Collections.nCopies(120, "a")) + ".pdf";
        String result = sanitize(longName);
        assertEquals(100, result.length()); assertTrue(result.endsWith(".pdf"));
    }
    @Test void j10() {
        for (String ext : new String[]{"md","txt","pdf","doc","docx","xls","xlsx","ppt","pptx","png","jpg","jpeg","zip"})
            assertEquals("sample."+ext, sanitize("sample."+ext));
    }
    @Test void j11() { assertThrows(BusinessException.class, () -> sanitize("report.pdf.exe")); }
    @Test void j12() { assertThrows(BusinessException.class, () -> sanitize("README")); }
    @Test void j13() {
        JwtUtil jwt = new JwtUtil(); String token = jwt.generateToken("42", "测试用户");
        assertTrue(jwt.validateToken(token)); assertEquals("42", jwt.getUserIdFromToken(token));
        assertEquals("测试用户", jwt.parseToken(token).get("nickname"));
    }
    @Test void j14() { JwtUtil jwt = new JwtUtil(); assertFalse(jwt.validateToken(null)); assertNull(jwt.getUserIdFromToken("bad")); }
    @Test void j15() { JwtUtil jwt = new JwtUtil(); assertFalse(jwt.validateToken(jwt.generateToken("1","x") + "broken")); }
    @Test void j16() {
        String secret = (String)ReflectionTestUtils.getField(JwtUtil.class, "SECRET");
        String token = Jwts.builder().setExpiration(new Date(System.currentTimeMillis()-10000))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)), SignatureAlgorithm.HS256).compact();
        assertFalse(new JwtUtil().validateToken(token));
    }
    private JwtInterceptor interceptor() {
        JwtInterceptor interceptor = new JwtInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtUtil", new JwtUtil());
        return interceptor;
    }
    @Test void j17() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("OPTIONS", "/api/v1/file/list");
        assertTrue(interceptor().preHandle(req, new MockHttpServletResponse(), new Object()));
    }
    @Test void j18() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        assertFalse(interceptor().preHandle(new MockHttpServletRequest(), res, new Object()));
        assertEquals(200, res.getStatus()); assertTrue(res.getContentAsString().contains("401"));
    }
    @Test void j19() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(); req.addHeader("Authorization", "Basic abc");
        assertFalse(interceptor().preHandle(req, new MockHttpServletResponse(), new Object()));
    }
    @Test void j20() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(); req.addHeader("Authorization", "Bearer forged");
        MockHttpServletResponse res = new MockHttpServletResponse();
        assertFalse(interceptor().preHandle(req, res, new Object())); assertTrue(res.getContentAsString().contains("10003"));
    }
    @Test void j21() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest(); req.addHeader("Authorization", "Bearer " + new JwtUtil().generateToken("42", "x"));
        assertTrue(interceptor().preHandle(req, new MockHttpServletResponse(), new Object())); assertEquals("42", req.getAttribute("userId"));
    }
    private Task task;
    private TaskMapper tasks;
    private NotifyService notify;
    private TaskService service() {
        task = new Task(); task.setId(7L); task.setProjectId(1L); task.setStatus("pending"); task.setProgress(0); task.setName("task");
        tasks = mock(TaskMapper.class); when(tasks.selectById(7L)).thenReturn(task);
        notify = mock(NotifyService.class);
        TaskService service = new TaskService();
        ReflectionTestUtils.setField(service,"taskMapper",tasks); ReflectionTestUtils.setField(service,"notifyService",notify);
        ReflectionTestUtils.setField(service,"projectMapper",mock(ProjectMapper.class));
        ReflectionTestUtils.setField(service,"userMapper",mock(UserMapper.class));
        return service;
    }
    private void progress(int value, String status) {
        TaskService service = service(); TaskProgressRequest req = new TaskProgressRequest(); req.setProgress(value);
        service.updateTaskProgress(7L,req); assertEquals(value,task.getProgress()); assertEquals(status,task.getStatus());
        verify(tasks).updateById(task);
    }
    @Test void j22() { progress(0,"pending"); }
    @Test void j23() { progress(1,"in_progress"); }
    @Test void j24() { progress(99,"in_progress"); }
    @Test void j25() { progress(100,"completed"); assertNotNull(task.getCompletedAt()); }
    @Test void j26() { TaskService s=service(); assertThrows(BusinessException.class,()->s.getTaskDetail(999L)); verify(tasks,never()).updateById(any()); }
    @Test void j27() { TaskService s=service(); s.completeTask(7L,null,1L); assertEquals("completed",task.getStatus()); assertEquals(100,task.getProgress()); assertNotNull(task.getCompletedAt()); }
    @Test void j28() { TaskService s=service(); CompleteTaskRequest r=new CompleteTaskRequest(); r.setRemark("完成备注"); s.completeTask(7L,r,1L); assertEquals("完成备注",task.getCompletionRemark()); }
    @Test void j29() { TaskService s=service(); s.abandonTask(7L,null); assertEquals("abandoned",task.getStatus()); }
    @Test void j30() { TaskService s=service(); AbandonTaskRequest r=new AbandonTaskRequest(); r.setReason("需要重分工"); s.abandonTask(7L,r); assertEquals("需要重分工",task.getCompletionRemark()); }
    @Test void j31() { TaskService s=service(); TransferTaskRequest r=new TransferTaskRequest(); r.setNewAssigneeId(9L); s.transferTask(7L,r,1L); assertEquals(9L,task.getAssigneeId()); verify(notify).sendMessage(eq(9L),eq(1L),eq("task"),anyString(),anyString()); }
    @Test void j32() { TaskService s=service(); s.deleteTask(7L); verify(tasks).deleteById(7L); }
    @Test void j33() { TaskService s=service(); UpdateTaskRequest r=new UpdateTaskRequest(); r.setName("new"); s.updateTask(7L,r); assertEquals("new",task.getName()); assertEquals("pending",task.getStatus()); }
    @Test void j34() {
        try (javax.validation.ValidatorFactory factory=Validation.buildDefaultValidatorFactory()) {
            Validator v=factory.getValidator(); TaskProgressRequest r=new TaskProgressRequest();
            assertFalse(v.validate(r).isEmpty());
            for(int value:new int[]{-1,101}) { r.setProgress(value); assertFalse(v.validate(r).isEmpty()); }
            for(int value:new int[]{0,1,99,100}) { r.setProgress(value); assertTrue(v.validate(r).isEmpty()); }
        }
    }
    @Test void j35() {
        WechatService s=new WechatService();
        assertThrows(BusinessException.class,()->s.code2openid("unused"));
    }
    @Test void j36() {
        TaskService s=service(); ProjectMapper p=mock(ProjectMapper.class);
        when(p.selectList(any())).thenReturn(Collections.emptyList()); ReflectionTestUtils.setField(s,"projectMapper",p);
        assertEquals(0,s.getMyTasks(1L,null,null,1,20).get("total"));
        assertTrue(s.getDueSoonTasks(1L,7).isEmpty()); verify(tasks,never()).selectList(any());
    }
    @Test void j37() {
        assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize",
            new MockMultipartFile("file","tmp_file","text/plain",new byte[]{1}), "danger.exe"));
    }
    @Test void j38() {
        assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize",
            new MockMultipartFile("file","tmp_file","text/plain",new byte[0]), "safe.txt"));
    }
    @Test void j39() {
        assertEquals("safe.txt", ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize",
            new MockMultipartFile("file","tmp_file","text/plain",new byte[]{1}), "../../safe.txt"));
    }
    @Test void j40() {
        MultipartFile file=mock(MultipartFile.class); when(file.getSize()).thenReturn(50L*1024*1024+1);
        assertThrows(BusinessException.class, () -> ReflectionTestUtils.invokeMethod(new FileService(), "validateAndSanitize",file,"safe.txt"));
    }
    private void notification(String type, UserSubscription subscription, boolean expected) {
        NotifyService s=new NotifyService(); MessageMapper messages=mock(MessageMapper.class);
        UserSubscriptionMapper preferences=mock(UserSubscriptionMapper.class);
        when(preferences.selectOne(any())).thenReturn(subscription);
        ReflectionTestUtils.setField(s,"messageMapper",messages);
        ReflectionTestUtils.setField(s,"userSubscriptionMapper",preferences);
        s.sendMessage(1L,2L,type,"标题","内容");
        verify(messages,times(expected?1:0)).insert(any());
    }
    @Test void j41() { UserSubscription sub=new UserSubscription(); sub.setTaskRemind(0); notification("task",sub,false); }
    @Test void j42() { UserSubscription sub=new UserSubscription(); sub.setAuditNotify(0); notification("audit",sub,false); }
    @Test void j43() { UserSubscription sub=new UserSubscription(); sub.setTaskRemind(1); notification("task",sub,true); }
    @Test void j44() { notification("task",null,true); }
    private ShareService shareService(String permission, File file) {
        ShareService s=new ShareService(); ShareMapper shares=mock(ShareMapper.class);
        Share share=new Share(); share.setId(1L); share.setProjectId(2L); share.setPermissionLevel(permission);
        when(shares.selectOne(any())).thenReturn(share);
        FileMapper files=mock(FileMapper.class); when(files.selectById(7L)).thenReturn(file);
        ReflectionTestUtils.setField(s,"shareMapper",shares); ReflectionTestUtils.setField(s,"fileMapper",files);
        ReflectionTestUtils.setField(s,"shareAccessLogMapper",mock(ShareAccessLogMapper.class));
        return s;
    }
    @Test void j45() { assertThrows(BusinessException.class,()->shareService("flow_only",null).fileDownload("t",7L)); }
    @Test void j46() { assertThrows(BusinessException.class,()->shareService("flow_only",null).filePreview("t",7L)); }
    @Test void j47() {
        File file=new File(); file.setProjectId(999L); file.setStatus("approved");
        assertThrows(BusinessException.class,()->shareService("flow_download",file).fileDownload("t",7L));
    }
    @Test void j48() {
        File file=new File(); file.setProjectId(2L); file.setStatus("pending");
        assertThrows(BusinessException.class,()->shareService("flow_download",file).fileDownload("t",7L));
    }
    @Test void j49() {
        File file=new File(); file.setProjectId(2L); file.setStatus("approved"); file.setUrl("/files/test/report.txt");
        assertEquals("/files/test/report.txt",shareService("flow_download",file).fileDownload("t",7L).get("downloadUrl"));
    }
    @Test void j50() {
        ProjectService s=new ProjectService(); ProjectMapper projects=mock(ProjectMapper.class);
        Project p=new Project(); p.setId(2L); p.setGroupId(3L); when(projects.selectById(2L)).thenReturn(p);
        GroupMemberMapper members=mock(GroupMemberMapper.class); when(members.selectCount(any())).thenReturn(0L);
        ReflectionTestUtils.setField(s,"projectMapper",projects); ReflectionTestUtils.setField(s,"groupMemberMapper",members);
        assertThrows(BusinessException.class,()->s.getProjectDetailForUser(2L,9L));
    }
}
