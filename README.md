# 프로그램 개요
사용자의 요구사항을 충족하면서 전기 소모를 최소화하는 제품을 추천

## 작동원리
![image](https://user-images.githubusercontent.com/67899393/109926137-f691f080-7d05-11eb-9211-8e824bbfb8d0.png)
에너지소비효율 등급표를 통하여 사용중인 제품의 정보를 입력받고 데이터베이스에서 사용자의 요구사항을 충족하는 제품을 읽어 온다.


# 주요 기능
Yolo 를 통한 object detectoion 과 OCR을 통하여 에너지소비효율등급표를 자동적으로 입력받는다.

### object detection
![image](https://user-images.githubusercontent.com/67899393/109926488-5b4d4b00-7d06-11eb-8a04-96eb24a2332b.png)
Object detection은 찾고 싶은 물체를 라벨링 하여 학습시킨 모델을 가지고 다른 이미지에서 그 물체를 찾는 기술

### OCR
![image](https://user-images.githubusercontent.com/67899393/109926598-7b7d0a00-7d06-11eb-9d1b-881b2450b36e.png)
OCR 광학 문자 인식 
사람이 쓰거나 인쇄한 문자의 영상을 이미지 스캐너로 획득하여 문자로 변환 하는 기술

위 두 기술을 통하여 핸드폰의 카메라로 에너지소비효율등급표를 인식하면 자동적으로 입력 되도록 하였다.
![image](https://user-images.githubusercontent.com/67899393/109926735-9ea7b980-7d06-11eb-9e1d-b078102b7d28.png)

## 검색 옵션
사용자의 요구사항 입력
![image](https://user-images.githubusercontent.com/67899393/109926875-d0b91b80-7d06-11eb-8af9-e382ec7be65b.png)

## 결과 화면
![image](https://user-images.githubusercontent.com/67899393/109926935-e3335500-7d06-11eb-9d51-99b76f56fd8a.png)
