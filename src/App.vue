<script>
export default {
  onLaunch() {
    // 每次打开/刷新站点都先落到登录页，由用户点「进入平台」确认进入。
    // H5 刷新页面会重新触发 onLaunch，因此深链/刷新也会回到登录页；
    // 登录态（token 7 天有效）仅用于预填昵称与免重新注册，不用于免确认进入。
    uni.reLaunch({ url: '/pages/login/login' })
  }
}
</script>

<style>
@import './styles/base.css';
@import './styles/utilities.css';
</style>
