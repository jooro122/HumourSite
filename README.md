# 💻 프로젝트 소개
* 1인 개발 - 유머 커뮤니티 사이트
* 기존 유머 커뮤니티들을 참고하여 프로그래밍이 가능한 기능들을 적용한 프로젝트

# 🏗️ 개발기간
* 개발 기간 : 2023.11.01 ~ 2023.12.13

# ⚙️ 프로젝트 개발 환경
* Frontend : HTML, CSS, JavaScript, AJAX, Thymeleaf, BootStrap

* Backend : JAVA 11, Spring Boot, Spring Data JPA, Spring Security

* Database : Hibernate, MariaDB

* OpenAPI : 네이버 소셜 로그인

# 📜프로젝트 구현기능
# 회원
* 관리자 기능 (모든 게시물과 댓글 제어 기능)
* Oauth2 를 활용한 네이버 소셜 로그인 구현
* 회원 가입시 @Valid를 활용한 유효성 검사 기능
* 회원 페이지의 회원 정보 수정, 회원 탈퇴, 내가 쓴 글 조회, 내가 좋아요 한 글 조회 기능
* Spring Security 로그인(UserDetails)과 소셜 로그인(OAuth2User)의 통합 인증을 위한 PrincipalDetail 클래스 활용

# 게시판

* 좋아요 n개 이상 게시물은 베스트 게시판에 표시(현 프로젝트에서는 1개 이상으로 설정)
* 썸머노트를 활용한 게시글 작성 기능(이미지 파일은 게시글 내용에 직접 삽입)
* 게시판 이름과 카테고리별로 게시물 DB 설계
* 게시물 별 댓글 작성 / 삭제 기능
* 제목, 내용, 작성자별 검색 기능

# 레이아웃
* 타임리프 레이아웃을 이용하여 공통 양식 생성 후 header / content / footer 로 나누어 페이지 관리
