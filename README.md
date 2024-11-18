## 팀원 역할분담

- 윤홍찬: 주문, 결제
- 이한주: 메뉴
- 장윤지: 음식점, 카테고리
- 조창현: 리뷰, 유저


## 프로젝트 목적/상세

배달 API 구현


## ERD

![image](https://github.com/user-attachments/assets/4d2e1cc7-1a82-4081-8f31-be2e2aa08ff3)


## 기술 스택

Spring Boot
PostgreSQL
Amazon EC2
Amazon RDS
Google Gemini


## 어려웠던 점

### 중첩 json에서 원하는 데이터 추출

Dto 클래스를 만들어서 한 번에 중첩 json을 매핑하고 싶었으나 하지 못해서 String 클래스로 매핑해서 해결

```json
{
    "candidates": [
        {
            "content": {
                "parts": [
                    {
                        "text": "엄마표 매콤한 김치가 듬뿍!  집김치의 시원한 매운맛이 일품인 김치만두입니다.  한 입 베어물면 중독되는 맛!\n"
                    }
                ],
                "role": "model"
            },
            "finishReason": "STOP",
            "avgLogprobs": -0.22993026177088419
        }
    ],
    "usageMetadata": {
        "promptTokenCount": 42,
        "candidatesTokenCount": 48,
        "totalTokenCount": 90
    },
    "modelVersion": "gemini-1.5-flash-002"
}
```

```java
    public String aiCreateMenuDescription(String requestText) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com")
                .path("/v1beta/models/gemini-1.5-flash-latest:generateContent")
                .queryParam("key", gemeniKey)
                .encode()
                .build()
                .toUri();

        String request = "{\n" +
                "    \"contents\": {\n" +
                "        \"parts\": [\n" +
                "            {\n" +
                "                \"text\": " + "\"" + requestText + "\"" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        try {
            // String(request) -> json
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(request);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, jsonObj, String.class);

            // json -> String(response)
            JSONObject jsonObject = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray candidates = (JSONArray) jsonObject.get("candidates");
            JSONObject content = (JSONObject) ((JSONObject) candidates.get(0)).get("content");
            JSONArray parts = (JSONArray) content.get("parts");
            String response = (String) ((JSONObject) parts.get(0)).get("text");

            if (response.contains("Please provide me")) {
                throw new IllegalArgumentException("AI에게 메뉴를 설명해달라고 작성해주세요.");
            }

            return response;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("AI에게 메뉴를 설명해달라고 작성해주세요.");
        }
    }
```

## 잘 된 점
