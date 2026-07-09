//按钮水波纹
function computeRippleStyles (element, event) {
  const { top, left } = element.getBoundingClientRect()
  const { clientWidth, clientHeight } = element

  const radius = Math.sqrt(clientWidth ** 2 + clientHeight ** 2) / 2
  const size = radius * 2

  const localX = event.clientX - left
  const localY = event.clientY - top

  const centerX = (clientWidth - radius * 2) / 2
  const centerY = (clientHeight - radius * 2) / 2

  const x = localX - radius
  const y = localY - radius

  return { x, y, centerX, centerY, size }
}
function createRipple (event) {
  const container = event.currentTarget
  const { x, y, centerX, centerY, size } = this.computeRippleStyles(container, event)
  const ripple = document.createElement('div')
  ripple.classList.add('myripple')
  ripple.style.opacity = `0`
  ripple.style.transform = `translate(${x}px, ${y}px) scale3d(.3, .3, .3)`
  ripple.style.width = `${size}px`
  ripple.style.height = `${size}px`
  ripple.style.position = `absolute`
  ripple.style.top = `0`
  ripple.style.left = `0`
  ripple.style['z-index'] = `100`
  ripple.style['border-radius'] = `50%`
  ripple.style['background-color'] = '#fad4a1'//`currentColor`
  ripple.style['opacity'] = `0`
  ripple.style.transition = 'transform 0.2s cubic-bezier(0.68, 0.01, 0.62, 0.6),' +
    'opacity 0.08s linear';
  ripple.style['will-change'] = 'transform, opacity';
  ripple.style['pointer-events'] = 'none';
  // 记录水波的创建时间
  ripple.dataset.createdAt = String(performance.now())

  const { position } = window.getComputedStyle(container)
  container.style.overflow = 'hidden'
  position === 'static' && (this.style.position = 'relative')

  container.appendChild(ripple)

  window.setTimeout(() => {
    ripple.style.transform = `translate(${centerX}px, ${centerY}px) scale3d(1, 1, 1)`
    ripple.style.opacity = `.25`
  })
}
function removeRipple (event) {
  const container = event.currentTarget
  const ripples = container.querySelectorAll('.myripple')
  if (!ripples.length) {
    return
  }

  const lastRipple = ripples[ripples.length - 1]
  // 通过水波的创建时间计算出扩散动画还需要执行多久，确保每一个水波都完整的执行了扩散动画
  const delay = 300 - performance.now() + Number(lastRipple.dataset.createdAt)

  setTimeout(() => {
    lastRipple.style.opacity = `0`

    setTimeout(() => lastRipple.parentNode?.removeChild(lastRipple), 300)
  }, delay)
}
