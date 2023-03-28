-- Andy Bodell
use CPSC4620Project;

DROP VIEW IF EXISTS ToppingPopularity;
CREATE VIEW ToppingPopularity AS
SELECT ToppingName as Topping, COUNT(ToppingName) + SUM(PizzaToppingRelationshipExtraToppings) as ToppingCount from topping a
LEFT JOIN pizzatoppingrelationship b ON a.ToppingID = b.PizzaToppingRelationshipToppingID
GROUP BY Topping
ORDER BY ToppingCount DESC, Topping DESC;

SELECT * FROM ToppingPopularity;

DROP VIEW IF EXISTS ProfitByPizza;
CREATE VIEW ProfitByPizza AS
SELECT PizzaCrust, PizzaSize, CAST(ROUND(SUM(PizzaPrice - PizzaCost), 5) AS DECIMAL(6,2)) as Profit, DATE(MAX(OrderTime)) as LastOrderDate FROM pizza
LEFT JOIN `order` ON PizzaOrderID = OrderID
GROUP BY PizzaCrust, PizzaSize
ORDER BY Profit DESC;

SELECT * FROM ProfitByPizza;

DROP VIEW IF EXISTS ProfitByOrderType;
CREATE VIEW ProfitByOrderType AS
SELECT OrderType as CustomerType, DATE_FORMAT(OrderTime, '%Y-%m') AS OrderMonth,
CAST(OrderPrice AS DECIMAL(6,2)) as TotalOrderPrice, CAST(OrderCost AS DECIMAL(6,2)) as TotalOrderCost, CAST(ROUND(OrderPrice - OrderCost, 5) AS DECIMAL(6,2)) as Profit
FROM `order`
UNION ALL 
SELECT
" ",
'Grand Total',
CAST(ROUND(SUM(OrderPrice), 5) AS DECIMAL(6,2)),
CAST(ROUND(SUM(OrderCost), 5) AS DECIMAL(6,2)),
CAST(ROUND(SUM(OrderPrice - OrderCost), 5) AS DECIMAL(6,2))
FROM `order`
ORDER BY OrderMonth;

SELECT * FROM ProfitByOrderType;