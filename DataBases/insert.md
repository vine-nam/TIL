oracle에서 insert를 한번에 해야 할때

```sql
NSERT ALL
  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)
  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)
  INTO mytable (column1, column2, column_n) VALUES (expr1, expr2, expr_n)
SELECT * FROM dual;
```
출처: https://www.techonthenet.com/oracle/questions/insert_rows.php
