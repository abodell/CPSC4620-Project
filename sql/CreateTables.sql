-- Andy Bodell
CREATE SCHEMA IF NOT EXISTS CPSC4620Project;
use CPSC4620Project;

CREATE TABLE IF NOT EXISTS topping(
    ToppingID INT NOT NULL AUTO_INCREMENT,
    ToppingName VARCHAR(255) NOT NULL,
    ToppingPrice FLOAT NOT NULL,
    ToppingCost FLOAT NOT NULL,
    ToppingPersonalAmt INT NOT NULL,
    ToppingMediumAmt INT NOT NULL,
    ToppingLargeAmt INT NOT NULL,
    ToppingXLargeAmt INT NOT NULL,
    ToppingInventory INT NOT NULL,
    PRIMARY KEY (ToppingID)
);

CREATE TABLE IF NOT EXISTS discount(
    DiscountID INT NOT NULL AUTO_INCREMENT,
    DiscountName VARCHAR(255) NOT NULL,
    DiscountPercentOff FLOAT NOT NULL,
    DiscountDollarsOff FLOAT NOT NULL,
    PRIMARY KEY (DiscountID)
);

CREATE TABLE IF NOT EXISTS customer(
    CustomerID INT NOT NULL AUTO_INCREMENT,
    CustomerFName VARCHAR(255) NOT NULL,
    CustomerLName VARCHAR(255) NOT NULL,
    CustomerPhoneNumber VARCHAR(255) NOT NULL,
    PRIMARY KEY (CustomerID)
);

CREATE TABLE IF NOT EXISTS `order`(
    OrderID INT NOT NULL AUTO_INCREMENT,
    OrderType VARCHAR(255) NOT NULL,
    OrderCost FLOAT NOT NULL,
    OrderPrice FLOAT NOT NULL,
    OrderTime DATETIME NOT NULL,
    OrderCustomerID INT NOT NULL,
    OrderIsComplete TINYINT NOT NULL,
    PRIMARY KEY (OrderID),
    FOREIGN KEY (OrderCustomerID) REFERENCES customer(CustomerID)
);

CREATE TABLE IF NOT EXISTS dineinorder(
    DineInOrderOrderID INT NOT NULL,
    DineInOrderTableNum INT NOT NULL,
    PRIMARY KEY (DineInOrderOrderID),
    FOREIGN KEY (DineInOrderOrderID) REFERENCES `order`(OrderID)
);

CREATE TABLE IF NOT EXISTS pickuporder(
    PickUpOrderOrderID INT NOT NULL,
    PickUpOrderPickedUp TINYINT NOT NULL,
    PRIMARY KEY (PickUpOrderOrderID),
    FOREIGN KEY (PickUpOrderOrderID) REFERENCES `order`(OrderID)
);

CREATE TABLE IF NOT EXISTS deliveryorder(
    DeliveryOrderOrderID INT NOT NULL,
    DeliveryOrderAddress VARCHAR(255) NOT NULL,
    DeliveryOrderDelivered TINYINT NOT NULL,
    PRIMARY KEY (DeliveryOrderOrderID),
    FOREIGN KEY (DeliveryOrderOrderID) REFERENCES `order`(OrderID)
);

CREATE TABLE IF NOT EXISTS baseprice(
    BasePricePizzaCrust VARCHAR(255) NOT NULL,
    BasePricePizzaSize VARCHAR(255) NOT NULL,
    BasePricePizzaCost FLOAT NOT NULL,
    BasePricePizzaPrice FLOAT NOT NULL,
    PRIMARY KEY (BasePricePizzaCrust, BasePricePizzaSize)
);

CREATE TABLE IF NOT EXISTS pizza(
    PizzaID INT NOT NULL AUTO_INCREMENT, 
    PizzaCrust VARCHAR(255) NOT NULL,
    PizzaSize VARCHAR(255) NOT NULL,
    PizzaCost FLOAT NOT NULL,
    PizzaPrice FLOAT NOT NULL,
    PizzaStatus TINYINT NOT NULL,
    PizzaOrderID INT NOT NULL,
    PRIMARY KEY (PizzaID),
    FOREIGN KEY (PizzaOrderID) REFERENCES `order`(OrderID),
    FOREIGN KEY (PizzaCrust, PizzaSize) REFERENCES baseprice(BasePricePizzaCrust, BasePricePizzaSize)
);

CREATE TABLE IF NOT EXISTS orderdiscount(
    OrderDiscountOrderID INT NOT NULL,
    OrderDiscountDiscountID INT NOT NULL,
    PRIMARY KEY (OrderDiscountOrderID, OrderDiscountDiscountID),
    FOREIGN KEY (OrderDiscountOrderID) REFERENCES `order`(OrderID),
    FOREIGN KEY (OrderDiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE IF NOT EXISTS pizzadiscount(
    PizzaDiscountPizzaID INT NOT NULL,
    PizzaDiscountDiscountID INT NOT NULL,
    PRIMARY KEY (PizzaDiscountPizzaID, PizzaDiscountDiscountID),
    FOREIGN KEY (PizzaDiscountPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY (PizzaDiscountDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE IF NOT EXISTS pizzatoppingrelationship(
    PizzaToppingRelationshipPizzaID INT NOT NULL,
    PizzaToppingRelationshipToppingID INT NOT NULL,
    PizzaToppingRelationshipExtraToppings TINYINT NOT NULL,
    PRIMARY KEY (PizzaToppingRelationshipPizzaID, PizzaToppingRelationshipToppingID),
    FOREIGN KEY (PizzaToppingRelationshipPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY (PizzaToppingRelationshipToppingID) REFERENCES topping(ToppingID)
);