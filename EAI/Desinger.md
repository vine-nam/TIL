TIBCO desinger에서 `JDBC Query` Activity에서 Select문 Fetch시 에러가 나는 경우가 있다.</br>
Fetch 말고 execute를 실행하면 timestamp type의 컬럼에서 자바 객체의 참조값이 출력되는 것을 볼수 있다.</br>
다음과 같이 해당문을 변경한 후 조회하면 정상 조회된다.</br>
`TO_CHAR(ALTDATE, 'YYYY/MM/DD HH24:MI:SS.FF6') ALTDATE`
