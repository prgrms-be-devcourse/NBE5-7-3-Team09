# NBE5-7-3-Team09
<img width="483" alt="스크린샷 2025-05-15 오후 4 44 36" src="https://github.com/user-attachments/assets/2b2df5ad-6f16-4637-8a7d-765a3c59d32c" />
<br>
팀 9글 3차 프로젝트 입니다.
프로그래머스 백엔드 데브코스 5기 7회차 9팀 **9글** 3차 프로젝트입니다.
<br>
<br>

# 🙋‍♀️ 프로젝트 소개
> **Readio – 스마트한 전자책 구독 플랫폼**
- 언제 어디서나 웹으로 책을 읽을 수 있는 온라인 전자책 구독 서비스입니다.
- 전자책 데이터는 네이버 클라우드에 안정적으로 저장되며, Elasticsearch를 통해 빠르고 정확한 검색 환경을 제공합니다.
- 사용자는 토스페이 결제를 통해 포인트를 충전하고, 충전된 포인트로 구독권을 구매할 수 있습니다.
- 회원가입, 구독 결제 및 취소 등의 주요 이벤트는 Gmail SMTP를 활용한 이메일 알림으로 실시간 안내됩니다.


<br>

## 👯 **팀원 소개**

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/iamjieunkim"><img src="https://avatars.githubusercontent.com/u/83564946?v=4" width="150px"/></a><br/>
      <a href="https://github.com/iamjieunkim"><b>김지은</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/kimsj0970"><img src="https://avatars.githubusercontent.com/u/53886275?v=4" width="150px"/></a><br/>
      <a href="https://github.com/kimsj0970"><b>김승중</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/chw0912"><img src="https://avatars.githubusercontent.com/u/95081400?v=4" width="150px"/></a><br/>
      <a href="https://github.com/chw0912"><b>최희웅</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/HwuanPage"><img src="https://avatars.githubusercontent.com/u/39082854?v=4" width="150px"/></a><br/>
      <a href="https://github.com/HwuanPage"><b>황성철</b></a>
    </td>
  </tr>
  <tr>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
  </tr>
</table>

<br>
<br>

## 📏 **프로젝트 규칙**
### ✅ Issue 규칙
<img width="796" alt="스크린샷 2025-05-09 오전 11 35 08" src="https://github.com/user-attachments/assets/27e57572-c61c-4958-8f39-e52ae7b3d16f" />

- 제목: [FE/BE] feat : 개발기능
- ex)[BE] feat: entity 생성
- 내용: 템플릿에 맞춰서 작성
- 라벨: `FE`, `BE`, `기능추가`, `리팩토링`, `레이아웃`, `에러`...
<br>

### ✅ 브랜치 타입 정리

| **타입** | **설명** |
| --- | --- |
| feat | 새로운 기능 개발 (기능 추가, API 구현 등) |
| fix | 버그 수정 (오류 해결, 예외 처리 등) |
| refactor | 리팩토링 (기능 변화 없이 코드 구조 개선) |
| docs | 문서 작업 (README 수정, API 명세 등) |
| test | 테스트 코드 작성 |
| chore | 설정, 빌드, 패키지 등 기타 작업 |
<br>

### **✏️ 브랜치 네이밍 예시**

| **작업 내용** | **브랜치명** |  |
| --- | --- | --- |
| entity 구현 | feat/entity-add/해당이슈번호 | feat/entity-add/1 |
| 주문 버그 수정 | fix/order-total-bug/해당이슈번호 | fix/order-total-bug/2 |
| JPA 설정 변경 | chore/jpa-config/해당이슈번호 | chore/jpa-config/3 |
<br>

### **📘 브랜치 사용 규칙**

- 모든 작업은 **develop에서 브랜치 생성**
- 기능 단위로 브랜치 생성, 기능 완료 후 Pull Request
- 병합 전 반드시 팀원 리뷰 & 테스트
- 병합 후 브랜치 삭제 (Delete branch 클릭)
<br>

### 🤯 commit 규칙
`git commit -m “[Feat] commit messages”`

- feat : 새로운 기능 추가, 기존의 기능을 요구 사항에 맞추어 수정
- fix : 기능에 대한 버그 수정
- build : 빌드 관련 수정
- chore : 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore
- docs : 문서(주석) 수정
- style : 코드 스타일, 포맷팅에 대한 수정
- refactor : 기능의 변화가 아닌 코드 리팩터링 ex) 변수 이름 변경
- release : 버전 릴리즈
- merge : 병합

<br>

### 🙃 PR 규칙

> 자주 커밋하고 PR은 최대한 자주 한번에 올리면 알아보기 힘들어요🥲
> 
- 제목: **[Feat]** 핵심적인 부분만 간략하게
- 내용: 템플릿에 맞춰서 작성
- 라벨: `FE`, `BE`, `기능추가`, `리팩토링`, `레이아웃`, `에러`...
