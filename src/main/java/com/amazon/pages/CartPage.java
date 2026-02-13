package com.amazon.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * CartPage - Page Object for Amazon Cart Page.
 * Contains locators and methods for cart page interactions and validations.
 */
public class CartPage extends BasePage {

    // Locators - Multiple selectors for Amazon cart page variations
    // Updated based on codegen and actual Amazon.in cart structure
    private static final String CART_ITEMS = "[data-name='Active Items'] .sc-list-item, .sc-list-item-content, [data-item-index], .sc-cart-item, .sc-list-item, div[data-asin]";
    private static final String CART_ITEM_TITLE = ".sc-product-title, .a-size-medium.sc-product-title, h2.sc-product-title, [data-item-index] .a-size-medium, .sc-product-title a, span.sc-product-title, h3 a";
    private static final String CART_ITEM_PRICE = ".sc-product-price, .a-price-whole, .sc-price";
    private static final String CART_ITEM_QUANTITY = "span[data-action='quantity-dropdown'], .a-dropdown-container select, select[name='quantity']";
    private static final String CART_SUBTOTAL = "#sc-subtotal-amount-activecart, .sc-subtotal";
    private static final String EMPTY_CART_MESSAGE = ".sc-empty-cart-header, h1:has-text('Your Amazon Cart is empty'), .sc-empty-cart-message, h1:has-text('Cart is empty')";
    private static final String CART_COUNT_BADGE = "#nav-cart-count";
    private static final String SHOPPING_CART_LABEL = "Shopping Cart"; // For aria label matching

    public CartPage(Page page) {
        super(page);
    }

    @Step("Navigate to cart page")
    public CartPage navigateToCart() {
        logger.info("Navigating to cart page");
        
        // Navigate to cart
        page.navigate("https://www.amazon.in/gp/cart/view.html");
        page.waitForLoadState();
        page.waitForTimeout(2000);
        try {
            page.waitForLoadState(LoadState.NETWORKIDLE);
        } catch (Exception e) {
            logger.debug("Network idle timeout, continuing");
        }
        
        // Wait for cart content to appear (either items or empty message)
        try {
            // Try multiple selectors to detect cart state
            boolean cartLoaded = false;
            String[] contentSelectors = {
                CART_ITEMS,
                EMPTY_CART_MESSAGE,
                ".sc-list-item",
                "[data-item-index]",
                "h1:has-text('Cart')"
            };
            
            for (String selector : contentSelectors) {
                try {
                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                    cartLoaded = true;
                    logger.debug("Cart content detected with selector: {}", selector);
                    break;
                } catch (Exception e) {
                    // Try next selector
                }
            }
            
            if (!cartLoaded) {
                logger.warn("Cart content not detected with standard selectors, proceeding anyway");
            }
        } catch (Exception e) {
            logger.debug("Cart content selector timeout, proceeding anyway: {}", e.getMessage());
        }
        
        return this;
    }

    @Step("Get cart items count")
    public int getCartItemsCount() {
        try {
            // Check if cart is empty first
            if (isCartEmpty()) {
                return 0;
            }
            
            // Try multiple selectors
            int count = 0;
            try {
                count = page.locator(CART_ITEMS).count();
            } catch (Exception e) {
                // Try alternative selector
                try {
                    count = page.locator("[data-item-index], .sc-list-item-content").count();
                } catch (Exception ex) {
                    // Try getting count from product titles
                    try {
                        count = page.locator(".sc-product-title, [data-item-index] .a-size-medium").count();
                    } catch (Exception ex2) {
                        logger.debug("Could not get cart count with any selector");
                    }
                }
            }
            
            logger.info("Cart items count: {}", count);
            return count;
        } catch (Exception e) {
            logger.warn("Could not get cart items count: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Check if cart is empty")
    public boolean isCartEmpty() {
        try {
            // Check for empty cart message
            boolean isEmpty = page.locator(EMPTY_CART_MESSAGE).isVisible();
            logger.info("Cart is empty: {}", isEmpty);
            return isEmpty;
        } catch (Exception e) {
            // If empty cart message not found, check if there are any items
            try {
                int itemCount = page.locator(CART_ITEMS).count();
                return itemCount == 0;
            } catch (Exception ex) {
                logger.debug("Could not determine if cart is empty");
                return false;
            }
        }
    }

    @Step("Check if product {productName} is in cart")
    public boolean isProductInCart(String productName) {
        logger.info("Checking if product '{}' is in cart", productName);
        
        if (isCartEmpty()) {
            logger.info("Cart is empty");
            return false;
        }

        try {
            List<String> cartItemTitles = getAllCartItemTitles();
            logger.info("Found {} items in cart. Titles: {}", cartItemTitles.size(), cartItemTitles);
            
            // Extract keywords from product name for better matching
            String[] searchKeywords = productName.toLowerCase().split("\\s+");
            
            for (String title : cartItemTitles) {
                String titleLower = title.toLowerCase();
                logger.debug("Comparing: '{}' with '{}'", productName, title);
                
                // Strategy 1: Exact substring match
                if (titleLower.contains(productName.toLowerCase()) ||
                    productName.toLowerCase().contains(titleLower.substring(0, Math.min(titleLower.length(), 30)))) {
                    logger.info("Product '{}' found in cart (exact match): {}", productName, title);
                    return true;
                }
                
                // Strategy 2: Keyword matching (at least 60% of keywords match)
                int matchCount = 0;
                for (String keyword : searchKeywords) {
                    if (keyword.length() > 2 && titleLower.contains(keyword)) {
                        matchCount++;
                    }
                }
                
                if (matchCount >= Math.max(2, searchKeywords.length * 0.6)) {
                    logger.info("Product '{}' found in cart (keyword match {}/{}): {}", 
                            productName, matchCount, searchKeywords.length, title);
                    return true;
                }
                
                // Strategy 3: Check for iPhone model numbers (e.g., "17 Pro Max")
                if (productName.toLowerCase().contains("iphone") && titleLower.contains("iphone")) {
                    // Extract model number (e.g., "17 Pro Max")
                    String modelPattern = productName.toLowerCase().replace("iphone", "").trim();
                    if (!modelPattern.isEmpty() && titleLower.contains(modelPattern)) {
                        logger.info("Product '{}' found in cart (model match): {}", productName, title);
                        return true;
                    }
                }
            }
            
            logger.warn("Product '{}' not found in cart. Available items: {}", productName, cartItemTitles);
            return false;
        } catch (Exception e) {
            logger.error("Error checking if product is in cart: {}", e.getMessage(), e);
            return false;
        }
    }

    @Step("Get all cart item titles")
    public List<String> getAllCartItemTitles() {
        List<String> titles = new ArrayList<>();
        try {
            if (isCartEmpty()) {
                return titles;
            }

            // Approach 0: Try using aria label (from codegen pattern)
            try {
                Locator shoppingCart = page.getByLabel(SHOPPING_CART_LABEL, new Page.GetByLabelOptions().setExact(true));
                if (shoppingCart.count() > 0) {
                    // Get all links within shopping cart (product titles are usually links)
                    var links = shoppingCart.locator("a[href*='/dp/'], a[href*='/gp/product/']").all();
                    for (var link : links) {
                        try {
                            String title = link.textContent().trim();
                            if (!title.isEmpty() && title.length() > 10 && !titles.contains(title)) {
                                titles.add(title);
                                logger.debug("Found cart item via aria label: {}", title);
                            }
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    if (!titles.isEmpty()) {
                        logger.info("Found {} items using aria label approach", titles.size());
                        return titles;
                    }
                }
            } catch (Exception e) {
                logger.debug("Aria label approach failed: {}", e.getMessage());
            }

            // Try multiple approaches to get cart items
            // Approach 1: Standard cart item selector
            try {
                var items = page.locator(CART_ITEMS).all();
                logger.info("Found {} items with standard selector", items.size());
                for (var item : items) {
                    try {
                        String title = item.locator(CART_ITEM_TITLE).textContent().trim();
                        if (!title.isEmpty()) {
                            titles.add(title);
                            logger.debug("Cart item: {}", title);
                        }
                    } catch (Exception e) {
                        logger.debug("Could not get title from standard item");
                    }
                }
            } catch (Exception e) {
                logger.debug("Standard selector failed: {}", e.getMessage());
            }
            
            // Approach 2: Direct product title selector (more comprehensive)
            if (titles.isEmpty()) {
                logger.info("Trying direct product title selector");
                try {
                    // Try multiple title selectors
                    String[] titleSelectors = {
                        ".sc-product-title",
                        ".sc-product-title a",
                        "span.sc-product-title",
                        "[data-item-index] .a-size-medium",
                        "h2.sc-product-title",
                        ".sc-list-item .a-size-medium",
                        "div[data-asin] .a-size-medium"
                    };
                    
                    for (String selector : titleSelectors) {
                        try {
                            var titleLocators = page.locator(selector).all();
                            logger.debug("Found {} elements with selector: {}", titleLocators.size(), selector);
                            for (var locator : titleLocators) {
                                try {
                                    String title = locator.textContent().trim();
                                    if (!title.isEmpty() && title.length() > 5 && !titles.contains(title)) {
                                        titles.add(title);
                                        logger.debug("Found cart item via title selector '{}': {}", selector, title);
                                    }
                                } catch (Exception e) {
                                    // Skip this locator
                                }
                            }
                            if (!titles.isEmpty()) {
                                break; // Found titles, no need to try more selectors
                            }
                        } catch (Exception e) {
                            logger.debug("Selector '{}' failed: {}", selector, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Title selector approach failed: {}", e.getMessage());
                }
            }
            
            // Approach 3: Get all text that looks like product titles (fallback)
            if (titles.isEmpty()) {
                logger.info("Trying to extract product titles from page text");
                try {
                    // Try finding product titles by looking for links or text in cart items
                    var allLinks = page.locator(".sc-list-item a, [data-item-index] a, div[data-asin] a").all();
                    for (var link : allLinks) {
                        try {
                            String text = link.textContent().trim();
                            String href = link.getAttribute("href");
                            // If it's a product link and has meaningful text
                            if (href != null && href.contains("/dp/") && text.length() > 10 && text.length() < 200) {
                                if (!titles.contains(text)) {
                                    titles.add(text);
                                    logger.debug("Found product title via link: {}", text);
                                }
                            }
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    
                    // Also try h2 and product-like elements
                    if (titles.isEmpty()) {
                        var allHeadings = page.locator("h2, .a-size-medium, .a-text-bold").all();
                        for (var heading : allHeadings) {
                            try {
                                String text = heading.textContent().trim();
                                if (text.length() > 10 && text.length() < 200) {
                                    titles.add(text);
                                    logger.debug("Found potential product title: {}", text);
                                }
                            } catch (Exception e) {
                                // Skip
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Text extraction approach failed: {}", e.getMessage());
                }
            }
            
            // Log final result
            if (titles.isEmpty()) {
                logger.warn("No cart item titles found. Taking screenshot for debugging.");
                try {
                    byte[] screenshot = page.screenshot();
                    logger.debug("Screenshot taken for cart page debugging");
                } catch (Exception e) {
                    logger.debug("Could not take screenshot");
                }
            }
        } catch (Exception e) {
            logger.error("Error getting cart item titles: {}", e.getMessage(), e);
        }
        return titles;
    }

    @Step("Get product details for {productName}")
    public CartItemDetails getProductDetails(String productName) {
        logger.info("Getting product details for: {}", productName);
        
        if (isCartEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        try {
            int itemCount = page.locator(CART_ITEMS).count();
            for (int i = 0; i < itemCount; i++) {
                Locator item = page.locator(CART_ITEMS).nth(i);
                String title = item.locator(CART_ITEM_TITLE).textContent().trim();
                
                if (title.toLowerCase().contains(productName.toLowerCase()) ||
                    productName.toLowerCase().contains(title.toLowerCase().substring(0, Math.min(title.length(), 30)))) {
                    
                    // Get price
                    String price = "";
                    try {
                        price = item.locator(CART_ITEM_PRICE).textContent().trim();
                    } catch (Exception e) {
                        logger.debug("Could not get price");
                    }

                    // Get quantity
                    int quantity = 1;
                    try {
                        String qtyText = item.locator(CART_ITEM_QUANTITY).textContent().trim();
                        quantity = Integer.parseInt(qtyText.replaceAll("[^0-9]", ""));
                    } catch (Exception e) {
                        logger.debug("Could not get quantity, defaulting to 1");
                    }

                    CartItemDetails details = new CartItemDetails(title, price, quantity);
                    logger.info("Product details: {}", details);
                    return details;
                }
            }
            
            throw new RuntimeException("Product '" + productName + "' not found in cart");
        } catch (Exception e) {
            logger.error("Error getting product details: {}", e.getMessage());
            throw e;
        }
    }

    @Step("Verify product title matches {expectedTitle}")
    public boolean verifyProductTitle(String productName, String expectedTitle) {
        try {
            CartItemDetails details = getProductDetails(productName);
            boolean matches = details.getTitle().toLowerCase().contains(expectedTitle.toLowerCase()) ||
                             expectedTitle.toLowerCase().contains(details.getTitle().toLowerCase().substring(0, Math.min(details.getTitle().length(), 30)));
            logger.info("Product title verification: {}", matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying product title: {}", e.getMessage());
            return false;
        }
    }

    @Step("Verify product quantity is {expectedQuantity}")
    public boolean verifyProductQuantity(String productName, int expectedQuantity) {
        try {
            CartItemDetails details = getProductDetails(productName);
            boolean matches = details.getQuantity() == expectedQuantity;
            logger.info("Product quantity verification: expected={}, actual={}, matches={}", 
                    expectedQuantity, details.getQuantity(), matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying product quantity: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Inner class to hold cart item details
     */
    public static class CartItemDetails {
        private final String title;
        private final String price;
        private final int quantity;

        public CartItemDetails(String title, String price, int quantity) {
            this.title = title;
            this.price = price;
            this.quantity = quantity;
        }

        public String getTitle() {
            return title;
        }

        public String getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return String.format("CartItemDetails{title='%s', price='%s', quantity=%d}", 
                    title, price, quantity);
        }
    }
}
