- 테이블 ddl
```sql
SELECT DBMS_METADATA.GET_DDL('TABLE', 'table_name', 'user_name') FROM DUAL;
```

- 외래키(FK) ddl 
```sql
SELECT DBMS_METADATA.GET_DEPENDENT_DDL('REF_CONSTRAINT', 'table_name', 'user_name') FROM DUAL;
```


- 트리거 컴파일
```sql
ALTER TRIGGER user_name.trigger_name COMPILE;
```
trigger가 `INVALID`일때 다음 명령어를 실행하면 error 메시지를 확인 할 수 있다.<br/>
error를 수정 한 후 다시 compile하면 제대로 수정 되었을 경우 VALID로 바뀐다.
