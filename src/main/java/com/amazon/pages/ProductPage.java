package com.amazon.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

import java.util.List;

/**
 * ProductPage - Page Object for Amazon Product Detail Page.
 * Contains locators and methods for product page interactions.
 */
public class ProductPage extends BasePage {

    // Locators
    private static final String PRODUCT_TITLE = "#productTitle";
    private static final String ADD_TO_CART_BUTTON = "a-autoid-1-announce";
    private static final String BUY_NOW_BUTTON = "#buy-now-button";
    private static final String PRICE = "span.a-price-whole";
    private static final String CART_CONFIRMATION = "#NATC_SMART_WAGON_CONF_MSG_SUCCESS";
    private static final String CART_COUNT = "#nav-cart-count";
    private static final String CART_ICON = "#nav-cart";
    private static final String QUANTITY_SELECTOR = "#quantity";
    private static final String SIDE_SHEET_CLOSE = "button[data-action='a-modal-close']";

    public ProductPage(Page page) {
        super(page);
    }

    @Step("Get product title")
    public String getProductTitle() {
        try {
            waitForSelector("span#productTitle", 10000);
            String title = page.locator("span#productTitle").textContent().trim();
            logger.info("Product title: {}", title);
            return title;
        } catch (Exception e) {
            logger.warn("Could not get product title: {}", e.getMessage());
            return "";
        }
    }

    @Step("Verify product page is displayed")
    public boolean isProductPageDisplayed() {
        try {
            waitForSelector("span#productTitle", 10000);
            boolean displayed = page.locator("span#productTitle").isVisible();
            logger.info("Product page displayed: {}", displayed);
            return displayed;
        } catch (Exception e) {
            logger.error("Product page verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Step("Click Add to Cart button")
    public ProductPage clickAddToCart() {
        logger.info("Clicking Add to Cart button");
        try {
            // Wait for page to be fully loaded
            page.waitForLoadState();
            page.waitForTimeout(3000); // Give more time for same-tab navigation
            
            // Handle any popup/overlay that might appear
            handlePopupsBeforeAddToCart();
            page.waitForTimeout(2000);

            // Strategy 1: Use getByRole (from codegen - most reliable)
            try {
                logger.info("Attempting Add to Cart via role-based selector");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                        .setName("Add to cart"))
                        .click();
                logger.info("Add to Cart clicked successfully using role-based selector");
                page.waitForLoadState();
                page.waitForTimeout(3000);
                return this;
            } catch (Exception e) {
                logger.debug("Role-based Add to Cart failed, trying alternative methods: {}", e.getMessage());
            }

            // Strategy 2: Click via JavaScript (fallback)
            logger.info("Attempting Add to Cart via JavaScript click");
            String jsCode = "() => { " +
                    "const buttons = document.querySelectorAll('#add-to-cart-button'); " +
                    "for (let btn of buttons) { " +
                    "  if (btn.offsetParent !== null || btn.type === 'submit') { " +
                    "    btn.click(); " +
                    "    return 'clicked'; " +
                    "  } " +
                    "} " +
                    "const submitBtn = document.querySelector('input[name=\"submit.add-to-cart\"]'); " +
                    "if (submitBtn) { submitBtn.click(); return 'clicked-submit'; } " +
                    "const form = document.querySelector('#addToCart'); " +
                    "if (form) { form.submit(); return 'form-submitted'; } " +
                    "return 'not-found'; " +
                    "}";
            Object result = page.evaluate(jsCode);

            logger.info("Add to Cart result: {}", result);

            if ("not-found".equals(result)) {
                // Strategy 3: Try the visible "Add to Cart" button via locator
                logger.warn("JavaScript click failed, trying locator approach");
                Locator visibleBtn = page.locator("#add-to-cart-button:visible, span#submit\\.add-to-cart input");
                if (visibleBtn.count() > 0) {
                    visibleBtn.first().click(new Locator.ClickOptions().setForce(true));
                } else {
                    throw new RuntimeException("Could not find Add to Cart button using any method");
                }
            }

            logger.info("Add to Cart button clicked successfully");

            // Wait for confirmation or page update
            page.waitForLoadState();
            page.waitForTimeout(3000);
        } catch (Exception e) {
            logger.error("Error clicking Add to Cart: {}", e.getMessage());
            throw e;
        }
        return this;
    }
    
    @Step("Click on 'item in cart' link to navigate to cart")
    public void clickItemInCartLink() {
        logger.info("Clicking 'item in cart' link");
        try {
            // Wait a bit for the link to appear after adding to cart
            page.waitForTimeout(2000);
            
            // Use role-based selector (from codegen)
            try {
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                        .setName("item in cart"))
                        .click();
                logger.info("Clicked 'item in cart' link using role-based selector");
                page.waitForLoadState();
                return;
            } catch (Exception e) {
                logger.debug("Role-based 'item in cart' link not found, trying alternatives: {}", e.getMessage());
            }
            
            // Fallback: Try alternative selectors
            String[] cartLinkSelectors = {
                "a:has-text('item in cart')",
                "a:has-text('Go to Cart')",
                "a:has-text('Cart')",
                "#hlb-view-cart",
                "a[href*='/cart']"
            };
            
            for (String selector : cartLinkSelectors) {
                try {
                    if (page.locator(selector).isVisible()) {
                        page.locator(selector).click();
                        logger.info("Clicked cart link using selector: {}", selector);
                        page.waitForLoadState();
                        return;
                    }
                } catch (Exception e) {
                    // Continue to next selector
                }
            }
            
            logger.warn("Could not find 'item in cart' link, navigating directly to cart");
            // Last resort: navigate directly
            page.navigate("https://www.amazon.in/gp/cart/view.html");
            page.waitForLoadState();
        } catch (Exception e) {
            logger.error("Error clicking 'item in cart' link: {}", e.getMessage());
            // Fallback: navigate directly to cart
            page.navigate("https://www.amazon.in/gp/cart/view.html");
            page.waitForLoadState();
        }
    }

    @Step("Handle popups before Add to Cart")
    private void handlePopupsBeforeAddToCart() {
        try {
            // Close any side sheets or modals
            if (page.locator(SIDE_SHEET_CLOSE).isVisible()) {
                page.locator(SIDE_SHEET_CLOSE).click();
                page.waitForTimeout(500);
            }
        } catch (Exception e) {
            logger.debug("No popups to handle");
        }

        // Dismiss any "No thanks" for offers/protection plans
        try {
            Locator noThanks = page.locator("button:has-text('No thanks'), a:has-text('No thanks'), #attachSiNoCoverage");
            if (noThanks.count() > 0 && noThanks.first().isVisible()) {
                logger.info("Dismissing offer overlay");
                noThanks.first().click();
                page.waitForTimeout(1000);
            }
        } catch (Exception e) {
            logger.debug("No offer overlay to dismiss");
        }

        // Scroll down to the Add to Cart section
        try {
            String scrollJs = "() => { " +
                    "const addToCartSection = document.querySelector('#addToCart, #buybox, #add-to-cart-button'); " +
                    "if (addToCartSection) { " +
                    "  addToCartSection.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                    "  return 'scrolled'; " +
                    "} " +
                    "return 'not-found'; " +
                    "}";
            Object scrollResult = page.evaluate(scrollJs);
            logger.debug("Scroll result: {}", scrollResult);
            page.waitForTimeout(1500);
        } catch (Exception e) {
            logger.debug("Could not scroll to Add to Cart section: {}", e.getMessage());
        }
        
        // Handle iframes that might contain add-to-cart button
        try {
            handleIframes();
        } catch (Exception e) {
            logger.debug("Iframe handling completed or not needed");
        }
    }

    @Step("Verify item was added to cart")
    public boolean isItemAddedToCart() {
        try {
            // Wait longer for Amazon's cart confirmation to appear
            page.waitForTimeout(5000);

            // Multiple checks for cart addition confirmation
            boolean confirmationVisible = false;

            // Check 1: Cart confirmation message (wait for it with timeout)
            try {
                page.waitForSelector(CART_CONFIRMATION, new Page.WaitForSelectorOptions()
                        .setTimeout(10000)
                        .setState(WaitForSelectorState.VISIBLE));
                confirmationVisible = true;
                logger.info("Cart confirmation message found");
            } catch (Exception e) {
                logger.debug("Cart confirmation message not visible: {}", e.getMessage());
            }

            // Check 2: Look for "Added to Cart" or similar text in various locations
            if (!confirmationVisible) {
                try {
                    // Try multiple selectors for confirmation
                    String[] confirmationSelectors = {
                        "text=Added to Cart",
                        "text=added to Cart",
                        "text=Added to your Cart",
                        "#sw-atc-confirmation",
                        "[id*='atc-confirmation']",
                        ".a-alert-success",
                        "[data-action='add-to-cart-confirmation']"
                    };
                    
                    for (String selector : confirmationSelectors) {
                        try {
                            if (page.locator(selector).isVisible()) {
                                confirmationVisible = true;
                                logger.info("Found confirmation using selector: {}", selector);
                                break;
                            }
                        } catch (Exception e) {
                            // Continue to next selector
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Alternative confirmation not found");
                }
            }

            // Check 3: Verify cart count changed (wait for it to update)
            if (!confirmationVisible) {
                try {
                    // Wait a bit more for cart count to update
                    page.waitForTimeout(3000);
                    
                    // Check cart count multiple times as it might update asynchronously
                    for (int i = 0; i < 5; i++) {
                        try {
                            String cartCount = page.locator(CART_COUNT).textContent().trim();
                            logger.info("Cart count check {}: {}", i + 1, cartCount);
                            
                            if (!cartCount.equals("0") && !cartCount.isEmpty() && !cartCount.equals("")) {
                                confirmationVisible = true;
                                logger.info("Cart count updated to: {}", cartCount);
                                break;
                            }
                        } catch (Exception e) {
                            logger.debug("Could not read cart count on attempt {}", i + 1);
                        }
                        page.waitForTimeout(1000);
                    }
                } catch (Exception e) {
                    logger.debug("Could not check cart count: {}", e.getMessage());
                }
            }

            // Check 4: Look for URL change or page navigation indicating cart addition
            if (!confirmationVisible) {
                try {
                    String currentUrl = page.url();
                    if (currentUrl.contains("/cart") || currentUrl.contains("add-to-cart")) {
                        confirmationVisible = true;
                        logger.info("URL indicates cart addition: {}", currentUrl);
                    }
                } catch (Exception e) {
                    logger.debug("Could not check URL");
                }
            }

            // Check 5: Check if there's a "Go to Cart" button or link that appeared
            if (!confirmationVisible) {
                try {
                    if (page.locator("a:has-text('Go to Cart'), a:has-text('Cart'), #hlb-view-cart").isVisible()) {
                        confirmationVisible = true;
                        logger.info("Found 'Go to Cart' button/link");
                    }
                } catch (Exception e) {
                    logger.debug("No 'Go to Cart' button found");
                }
            }

            logger.info("Item added to cart confirmation: {}", confirmationVisible);
            return confirmationVisible;
        } catch (Exception e) {
            logger.error("Error verifying cart addition: {}", e.getMessage());
            return false;
        }
    }

    @Step("Navigate to cart")
    public void goToCart() {
        logger.info("Navigating to cart page");
        click(CART_ICON);
        page.waitForLoadState();
    }

    @Step("Check if Add to Cart button is disabled or unavailable")
    public boolean isAddToCartDisabledOrUnavailable() {
        try {
            page.waitForLoadState();
            page.waitForTimeout(2000);
            Locator btn = page.locator("#add-to-cart-button, input[name='submit.add-to-cart']");
            if (btn.count() == 0) return true;
            return btn.first().isDisabled() || !btn.first().isVisible();
        } catch (Exception e) {
            logger.debug("Add to Cart button check failed: {}", e.getMessage());
            return true;
        }
    }

    @Step("Get cart item count")
    public String getCartItemCount() {
        try {
            String count = getText(CART_COUNT).trim();
            logger.info("Cart item count: {}", count);
            return count;
        } catch (Exception e) {
            logger.warn("Could not get cart count: {}", e.getMessage());
            return "0";
        }
    }

    @Step("Get product price")
    public String getProductPrice() {
        try {
            waitForSelector(PRICE, 5000);
            String price = getText(PRICE).trim();
            logger.info("Product price: {}", price);
            return price;
        } catch (Exception e) {
            logger.warn("Could not get product price: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Handle iframes that might be present on Amazon product pages
     */
    private void handleIframes() {
        try {
            List<com.microsoft.playwright.Frame> frames = page.frames();
            logger.info("Found {} frames on product page", frames.size());
            
            // Check if add-to-cart button is in an iframe
            for (com.microsoft.playwright.Frame frame : frames) {
                try {
                    if (frame.locator(ADD_TO_CART_BUTTON).count() > 0) {
                        logger.info("Found Add to Cart button in iframe: {}", frame.name());
                        // Note: We'll try to click from main page first, but if needed we can switch context
                    }
                } catch (Exception e) {
                    // Frame might not be accessible, continue
                }
            }
        } catch (Exception e) {
            logger.debug("Error checking iframes: {}", e.getMessage());
        }
    }

    /**
     * Handle the scenario where product opens in a new tab.
     * Switches to the newly opened page.
     */
    @Step("Switch to new product tab if opened")
    public ProductPage switchToNewTabIfOpened() {
        List<Page> pages = page.context().pages();
        if (pages.size() > 1) {
            Page newPage = pages.get(pages.size() - 1);
            logger.info("Switching to new tab. Total tabs: {}", pages.size());
            // Set viewport for new tab
            newPage.setViewportSize(1920, 1080);
            // We need to work with the new page
            return new ProductPage(newPage);
        }
        return this;
    }
}
