package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.common.Result;
import com.shopping.entity.Cart;
import com.shopping.entity.Product;
import com.shopping.mapper.CartMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public Result<?> addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            return Result.error(400, "Product not available");
        }

        Cart existing = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }

        return Result.success("Added to cart");
    }

    @Override
    public Result<?> updateCart(Long userId, Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            return Result.error(404, "Cart item not found");
        }

        if (quantity <= 0) {
            cartMapper.deleteById(cartId);
            return Result.success("Cart item removed");
        }

        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
        return Result.success("Cart updated");
    }

    @Override
    public Result<?> removeFromCart(Long userId, Long cartId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            return Result.error(404, "Cart item not found");
        }

        cartMapper.deleteById(cartId);
        return Result.success("Removed from cart");
    }

    @Override
    public Result<List<Map<String, Object>>> getCart(Long userId) {
        List<Cart> cartItems = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : cartItems) {
            Product product = productMapper.selectById(cart.getProductId());
            Map<String, Object> item = new HashMap<>();
            item.put("cartId", cart.getId());
            item.put("quantity", cart.getQuantity());
            item.put("productId", cart.getProductId());
            if (product != null) {
                item.put("productName", product.getName());
                item.put("productImage", product.getMainImage());
                item.put("price", product.getPrice());
                item.put("stock", product.getStock());
                item.put("status", product.getStatus());
            }
            result.add(item);
        }

        return Result.success(result);
    }

    @Override
    public Result<?> clearCart(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        return Result.success("Cart cleared");
    }
}
