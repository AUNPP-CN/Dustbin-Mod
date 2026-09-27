<template>
  <div class="site-footer">
    <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">赣ICP备2026016492号</a>
    <span class="divider">|</span>
    <a href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=36010802001440" target="_blank" rel="noopener noreferrer" class="ga-item">
      <img src="/images/ghs.png" alt="公安备案徽标" />赣公网安备 36010802001440号
    </a>
  </div>
</template>

<style scoped>
.site-footer {
  padding: 24px 16px;
  text-align: center;
  font-size: 13px;
  color: var(--vp-c-text-2);
  border-top: 1px solid var(--vp-c-divider);
}
.site-footer a {
  color: var(--vp-c-text-2);
  text-decoration: none;
}
.site-footer a:hover {
  color: var(--vp-c-brand-1);
}
.ga-item {
  display: inline-flex;
  align-items: center;
}
.ga-item img {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  display: block;
}
.divider {
  margin: 0 10px;
}
</style>
