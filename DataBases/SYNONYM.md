- ORACLE에서 시노님으로 TRUNCATE가 안된다.
- 시노님으로 TRUNCATE실행 시 에러내용: `ORA-00942: 테이블 또는 뷰가 존재하지 않습니다`
- 시노님일때 원본 테이블 명을 조회해서 처리했다.
- 시노님일때 원테이블 명을, 시노님이 아닐 때 입력값(아마 테이블 명)을 조회(반환) 한다.
```sql
SELECT DECODE(O.OBJECT_TYPE, 'SYNONYM', S.TABLE_NAME, O.OBJECT_NAME) TABLE_NAME
FROM USER_OBJECTS O, USER_SYNONYMS S
WHERE O.OBJECT_NAME = S.TABLE_NAME
AND (O.OBJECT_NAME = 'BARCODE_INFO_INT' OR SYNONYM_NAME = 'BARCODE_INFO_INT');
```
