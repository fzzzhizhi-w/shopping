import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    total: 0
  }),
  getters: {
    itemCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0)
  },
  actions: {
    setCart(items) {
      this.items = items
      this.total = items.reduce((sum, i) => sum + i.price * i.quantity, 0)
    },
    clearCart() {
      this.items = []
      this.total = 0
    }
  }
})
