# 🚉 Busan Subway Project
## 🔍 Overview
![image](https://github.com/Moon-ymin/programmers_before/assets/83321379/21c5a1e6-5d84-4501-8347-5b463533e3a2)
- BSP (2024년 6월 11일 ~ 2024년 6월 27일)
- 부산 지하철 운행 정보 앱 제작
#### 프로젝트 목표 및 목적
- 지하철 노선도의 구현
- 도착 정보를 받지 못하거나 소리를 듣지 못한 상황에도 타이머 기능을 통해 효율적인 승하차 유도
## 팀원 소개
|   Name   | 문영민 | 정에스더 |
| :------: | ----- | ------ |
| Profile  | <img src="https://github.com/Moon-ymin/subway-project/assets/83321379/0ba481db-106f-46ea-9fed-e6dd595ea1e5" width="150px"> |<img src="https://github.com/Moon-ymin/subway-project/assets/83321379/4a5e7f8d-cdc2-4536-9342-d9f489b8eb20" width="150px"> |
| Position | Front - 메인 화면 구현 <br> Back - 경로 알고리즘 | Front - 경로 확인 화면 구현 <br> Back - DB 설계   |
| Git  | [@Moon-ymin](https://github.com/Moon-ymin) | [@EstherOVO](https://github.com/EstherOVO) |
## 📝 Description
#### 상세 설명
(ppt)
#### 전체 기능
- 메인 화면
  - 지하철 노선도 축소, 확대, 이동 가능
  - 한 / 영 변환 버전 제공
  - 지하철 노선도 위 역 선택시 경로 선택 가능
  - 검색 창에서 역 이름 검색 가능
- 운행 확인 화면
  - 선택된 경로의 최단시간, 최소환승 경로 정보 제공
  - 한 / 영 변환 버전 제공
  - 총 소요 시간, 이동 역 수 정보 제공
  - 도착 시간까지의 타이머 제공
## 🎨 Design
#### Prototyping in [Figma](https://www.figma.com/proto/UiQQpYoMqnA4P2OYw7OGic/Untitled?node-id=0-1&t=UJ6WzVOE1EuSFaat-1)
![image](https://github.com/Moon-ymin/subway-project/assets/83321379/734880b0-c365-4966-bf3d-f07a4fbc5311)
#### 시스템 구성도
![image](https://github.com/Moon-ymin/programmers_before/assets/83321379/b34b2bee-9323-48ed-a3ad-27b478947c40)
#### ERD
![image](https://github.com/Moon-ymin/programmers_before/assets/83321379/32bac369-4425-481e-b133-135125c5e772)
#### 아키텍처 구조
(추가)
#### Android - FrontEnd
directory
```markdown
📦 Android
├─ src
│  ├─ main
│  │  ├─ java/com/busanit/busan_subway_project
│  │  │  ├─ config
│  │  │  ├─ controlle
│  │  │  ├─ metr
│  │  │  ├─ model
│  │  │  ├─ repo
│  │  │  ├─ service
│  │  │  ├─ BusanSubwayProjectApplication.java
│  │  │  └─ Subway.java
│  │  └─ resources
│  └─ test/java/com/busanit/busan_subway_project
│     └─ BusanSubwayProjectApplicationTests.java
├─ build.gradle
└─ settings.gradle
...
```
#### Server - BackEnd
directory
```markdown
📦 Spring
├─ src
│  └─ main
│     ├─ assets
│     │  └─ station_points.html
│     ├─ java/com/busanit/subway_project
│     │  ├─ adapter
│     │  ├─ alarm
│     │  ├─ fragment
│     │  ├─ helper
│     │  ├─ model
│     │  ├─ retrofit
│     │  ├─ MainActivity.kt
│     │  ├─ RouteCheckActivity.kt
│     │  └─ SplashActivity.kt
│     ├─ res
│     │  ├─ drawable
│     │  ├─ font
│     │  ├─ layout
│     │  ├─ menu
│     │  ├─ values
│     │  └─ xml
│     ├─ AndroidManifest.x
│     ├─ bsp_app_icon-playstore.png
│     └─ ic_launcher-playstore.png
├─ build.gradle
├─ gradle.properties
└─ settings.gradle
...
```
## 📒 License


