- 파일 합치기
```shell
type ./path/* > newFile.txt
```
path 밑에 있는 모든 파일(*)을 newFile.txt라는 이름으로 합친다.

- 파일 이름 일괄 변경
```shell
Dir | Rename-Item -NewName {$_.name -replace "old","new"}
```
현재 위치에 있는 모든 파일의 이름을 `old`에서 `new`로 바꾼다.<br/>
참고: <https://www.windowscentral.com/how-rename-multiple-files-bulk-windows-10>
