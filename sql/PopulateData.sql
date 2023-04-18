-- Andy Bodell
use CPSC4620Project;

DROP PROCEDURE IF EXISTS `MAKE_PIZZA`;
DROP PROCEDURE IF EXISTS `MAKE_CUSTOMER`;

DELIMITER $$
CREATE PROCEDURE `MAKE_PIZZA` (
    IN CRUST VARCHAR(255),
    IN SIZE VARCHAR(255),
    IN ORDERID INT,
    IN COST FLOAT,
    IN PRICE FLOAT
)
BEGIN
    INSERT INTO pizza VALUES (0, CRUST, SIZE, COST, PRICE, 1, ORDERID);
END
$$
CREATE PROCEDURE `MAKE_CUSTOMER` (
    IN FNAME VARCHAR(255),
    IN LNAME VARCHAR(255),
    IN PHONE VARCHAR(255)
)
BEGIN 
    INSERT INTO customer VALUES (0, FNAME, LNAME, PHONE);
END
$$
DELIMITER ;

INSERT INTO discount VALUES
(0, 'Employee', 15, 0),
(0, 'Lunch Special Medium', 0, 1.00),
(0, 'Lunch Special Large', 0, 2.00),
(0, 'Specialty Pizza', 0, 1.50),
(0, 'Gameday Special', 20, 0);

INSERT INTO topping VALUES 
(0, 'Pepperoni', 1.25, 0.2, 2, 2.75, 3.5, 4.5, 100),
(0, 'Sausage', 1.25, 0.15, 2.5, 3, 3.5, 4.25, 100),
(0, 'Ham', 1.5, 0.15, 2, 2.4, 3.25, 4, 78),
(0, 'Chicken', 1.75, 0.25, 1.5, 2, 2.25, 3, 56),
(0, 'Green Pepper', .5, 0.02, 1, 1.5, 2, 2.5, 79),
(0, 'Onion', 0.5, 0.02, 1, 1.5, 2, 2.75, 85),
(0, 'Roma Tomato', 0.75, 0.03, 2, 3, 3.5, 4.5, 86),
(0, 'Mushrooms', 0.75, 0.1, 1.5, 2, 2.5, 3, 52),
(0, 'Black Olives', 0.6, 0.1, 0.75, 1, 1.5, 2, 39),
(0, 'Pineapple', 1, 0.25, 1, 1.25, 1.75, 2, 15),
(0, 'Jalapenos', 0.5, 0.05, 0.5, 0.75, 1.25, 1.75, 64),
(0, 'Banana Peppers', 0.5, 0.05, 0.6, 1, 1.3, 1.75, 36),
(0, 'Regular Cheese', 1.5, 0.12, 2, 3.5, 5, 7, 250),
(0, 'Four Cheese Blend', 2, 0.15, 2, 3.5, 5, 7, 150),
(0, 'Feta Cheese', 2, 0.18, 1.75, 3, 4, 5.5, 75),
(0, 'Goat Cheese', 2, 0.2, 1.6, 2.75, 4, 5.5, 54),
(0, 'Bacon', 1.5, 0.25, 1, 1.5, 2, 3, 89);

INSERT INTO baseprice VALUES
('Thin', 'small', 0.5, 3),
('Original', 'small', 0.75, 3),
('Pan', 'small', 1, 3.5),
('Gluten-Free', 'small', 2, 4),
('Thin', 'medium', 1, 5),
('Original', 'medium', 1.5, 5),
('Pan', 'medium', 2.25, 6),
('Gluten-Free', 'medium', 3, 6.25),
('Thin', 'large', 1.25, 8),
('Original', 'large', 2, 8),
('Pan', 'large', 3, 9),
('Gluten-Free', 'large', 4, 9.5),
('Thin', 'x-large', 2, 10),
('Original', 'x-large', 3, 10),
('Pan', 'x-large', 4.5, 11.5),
('Gluten-Free', 'x-large', 6, 12.5);

-- INSERTING DUMMY DATA THAT WILL BE USED FOR ALL DINE-IN CUSTOMERS
INSERT INTO customer VALUES (9999, 'DINE-IN', 'CUSTOMER', '111111111');

-- FIRST ORDER
INSERT INTO `order` VALUES (0, 'DINE-IN', 3.68, 13.50, '2023-03-05 12:03:00', 9999, 1);
INSERT INTO dineinorder VALUES ((SELECT MAX(OrderID) from `order`), 14);

CALL MAKE_PIZZA('Thin', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.68, 13.50);

INSERT INTO pizzatoppingrelationship 
VALUES ((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 1),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT(ToppingID) FROM topping WHERE ToppingName LIKE 'Sausage'), 0);

INSERT INTO orderdiscount VALUES ((SELECT MAX(OrderID) from `order`), (SELECT DiscountID from discount WHERE DiscountName = 'Lunch Special Large'));

-- SECOND ORDER
INSERT INTO `order` VALUES (0, 'DINE-IN', 4.63, 17.35, '2023-03-03 12:05:00', 9999, 1);
INSERT INTO dineinorder VALUES ((SELECT MAX(OrderID) from `order`), 4);

CALL MAKE_PIZZA('Pan', 'Medium', (SELECT MAX(OrderID) FROM `order`), 3.23, 10.60);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Feta Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Black Olives'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Roma Tomato'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Mushrooms'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Banana Peppers'), 0);

INSERT INTO orderdiscount VALUES ((SELECT MAX(OrderID) from `order`), (SELECT DiscountID from discount where DiscountName = 'Lunch Special Medium')),
((SELECT MAX(OrderID) from `order`), (SELECT DiscountID from discount where DiscountName = 'Specialty Pizza'));

CALL MAKE_PIZZA('Original', 'Small', (SELECT MAX(OrderID) from `order`), 1.40, 6.75);

INSERT INTO pizzatoppingrelationship
VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Chicken'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Banana Peppers'), 0);

-- THIRD ORDER
INSERT INTO customer VALUES (0, 'Ellis', 'Beck', '864-254-5861');

INSERT INTO `order` VALUES (0, 'PICK-UP', 19.8, 64.5, '2023-03-03 21:30:00', (SELECT CustomerID from customer WHERE CustomerPhoneNumber LIKE '864-254-5861'), 1);
INSERT INTO pickuporder VALUES ((SELECT MAX(OrderID) from `order`), 1);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

CALL MAKE_PIZZA('Original', 'Large', (SELECT MAX(OrderID) FROM `order`), 3.30, 10.75);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0);

-- FOURTH ORDER
INSERT INTO `order` VALUES (0, 'DELIVERY', 16.86, 45.5, '2023-03-05 19:11:00', (SELECT CustomerID from customer WHERE CustomerPhoneNumber LIKE '864-254-5861'), 1);
INSERT INTO deliveryorder VALUES ((SELECT MAX(OrderID) from `order`), '115 Party Blvd, Anderson SC 29621', 1);

CALL MAKE_PIZZA('Original', 'x-large', (SELECT MAX(OrderID) FROM `order`), 5.59, 14.50);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Four Cheese Blend'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Sausage'), 0);

INSERT INTO orderdiscount VALUES 
((SELECT MAX(OrderID) from `order`), (SELECT DiscountID from discount WHERE DiscountName = 'Gameday Special'));

CALL MAKE_PIZZA('Original', 'x-large', (SELECT MAX(OrderID) FROM `order`), 5.59, 17);

INSERT INTO pizzatoppingrelationship VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Four Cheese Blend'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Ham'), 1),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pineapple'), 1);

INSERT INTO pizzadiscount VALUES 
((SELECT MAX(PizzaID) from pizza), (SELECT DiscountID from discount WHERE DiscountName = 'Specialty Pizza'));

CALL MAKE_PIZZA('Original', 'x-large', (SELECT MAX(OrderID) from `order`), 5.68, 14);
INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Four Cheese Blend'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Jalapenos'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Bacon'), 0);

-- FIFTH ORDER
INSERT INTO customer VALUES (0, 'Kurt', 'McKinney', '864-474-9953');

INSERT INTO `order` VALUES (0, 'PICK-UP', 7.85, 16.85, '2023-03-02 17:30:00', (SELECT CustomerID from customer WHERE CustomerPhoneNumber LIKE '864-474-9953'), 1);
INSERT INTO pickuporder VALUES ((SELECT MAX(OrderID) from `order`), 1);

CALL MAKE_PIZZA('Gluten-Free', 'x-large', (SELECT MAX(OrderID) from `order`), 7.85, 16.85);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Green Pepper'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Roma Tomato'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Goat Cheese'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Onion'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Mushrooms'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Black Olives'), 0);


INSERT INTO pizzadiscount VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT DiscountID from discount WHERE DiscountName = 'Specialty Pizza'));

-- SIXTH ORDER
INSERT INTO customer VALUES (0, 'Calvin', 'Sanders', '864-232-8944');

INSERT INTO `order` VALUES (0, 'DELIVERY', 3.20, 13.25, '2023-03-02 18:17:00', (SELECT CustomerID from customer WHERE CustomerPhoneNumber LIKE '864-232-8944'), 1);
INSERT INTO deliveryorder VALUES ((SELECT MAX(OrderID) from `order`), '6745 Wessex St Anderson SC 29621', 1);

CALL MAKE_PIZZA('Thin', 'large', (SELECT MAX(OrderID) from `order`), 3.20, 13.25);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Green Pepper'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Chicken'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Four Cheese Blend'), 1),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Onion'), 0),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Mushrooms'), 0);

-- SEVENTH ORDER
INSERT INTO customer VALUES (0, 'Lance', 'Benton', '864-878-5679');

INSERT INTO `order` VALUES (0, 'DELIVERY', 6.3, 24, '2023-03-06 20:32:00', (SELECT CustomerID from customer WHERE CustomerPhoneNumber LIKE '864-878-5679'), 1);
INSERT INTO deliveryorder VALUES ((SELECT MAX(OrderID) from `order`), '879 Suburban Home, Anderson, SC 29621', 1);

INSERT INTO orderdiscount VALUES
((SELECT MAX(OrderID) from `order`), (SELECT DiscountID from discount WHERE DiscountName = 'Employee'));

CALL MAKE_PIZZA('Thin', 'large', (SELECT MAX(OrderID) from `order`), 3.75, 12);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Four Cheese Blend'), 1);

CALL MAKE_PIZZA('Thin', 'large', (SELECT MAX(OrderID) from `order`), 2.55, 12);

INSERT INTO pizzatoppingrelationship VALUES
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Pepperoni'), 1),
((SELECT MAX(PizzaID) from pizza), (SELECT (ToppingID) FROM topping WHERE ToppingName LIKE 'Regular Cheese'), 0);
