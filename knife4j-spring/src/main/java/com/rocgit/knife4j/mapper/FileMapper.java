package com.rocgit.knife4j.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rocgit.knife4j.model.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
}
