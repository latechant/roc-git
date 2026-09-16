<script>
// H5 keeps the browser's original date input appearance. The mini-program
// template uses its native date picker through conditional compilation.
import { h } from 'vue'

export default {
  inheritAttrs: false,
  props: { value: { type: String, default: '' } },
  emits: ['update:value'],
  setup(props, { attrs, emit }) {
    return () => h('input', {
      ...attrs,
      type: 'date',
      value: props.value,
      class: ['original-native-date', attrs.class],
      onInput: event => emit('update:value', event.target.value)
    })
  }
}
</script>
