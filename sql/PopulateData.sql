-- Andy Bodell
use CPSC4620Project;

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
('Gluten-Free', 'small', 2, 4)
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