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