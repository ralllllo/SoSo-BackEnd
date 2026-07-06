# SoSo Chatbot View Schema

이 문서는 SoSo 챗봇이 조회할 수 있는 MySQL VIEW 목록과 사용 규칙을 정의한다.  
챗봇은 실제 원본 테이블을 직접 조회하지 않고, 아래에 정의된 VIEW만 기준으로 SELECT 조회를 수행한다.

---

## 0. SQL 생성 규칙

### 반드시 지켜야 할 규칙

1. SQL은 반드시 `SELECT` 문만 생성한다.
2. `INSERT`, `UPDATE`, `DELETE`, `DROP`, `CREATE`, `ALTER`, `TRUNCATE` 문은 절대 생성하지 않는다.
3. 원본 테이블은 직접 조회하지 않는다.
4. 아래에 등록된 VIEW만 사용한다.
5. 사용자의 매장 데이터는 반드시 `store_seq` 또는 해당 VIEW의 조회 기준 컬럼으로 필터링한다.
6. 카드 관련 조회에서 `billing_key` 같은 민감 정보는 절대 조회하지 않는다.
7. 사용자가 날짜를 명시하지 않으면 최근 데이터 또는 현재 월 기준으로 조회한다.
8. 사용자가 특정 건수만 원하지 않아도 목록 조회는 기본적으로 `LIMIT`을 사용한다.

### 사용 가능한 파라미터 예시

- `:storeSeq` : 현재 선택된 매장 번호
- `:userSeq` : 현재 로그인한 회원 번호
- `:partnerSeq` : 거래처 회원 번호
- `:month` : 조회 월, 예: `2026-07`
- `:date` : 조회 날짜, 예: `2026-07-01`
- `:stockName` : 재고명
- `:orderSeq` : 발주 번호
- `:paymentSeq` : 결제 번호

---

## 1. Available Views

현재 챗봇이 사용할 수 있는 VIEW 목록은 다음과 같다.

- `view_business_info`
- `view_card_select`
- `view_payment_history`
- `view_expense_history`
- `view_payment_order_map`
- `view_current_stock`
- `view_stock_batch_expiration`
- `view_stock_history`
- `view_order_summary`
- `view_order_detail`
- `view_staff_list`
- `view_staff_work_history`
- `view_group_buy_history`

---

## 2. View Definitions

---

### 2.1. view_business_info

사업자 회원과 해당 회원이 가진 매장 정보를 조회하는 VIEW이다.

한 줄(row)은 사업자 회원 1명과 그 회원의 매장 정보를 의미한다.

#### Columns

- `user_seq`: 회원 고유 번호
- `name`: 회원 이름
- `nickname`: 회원 닉네임
- `user_type`: 회원 구분
- `status`: 회원 상태
- `store_seq`: 매장 고유 번호
- `store_owner_user_seq`: 매장을 소유한 회원 고유 번호
- `company_name`: 상호명
- `ceo_name`: 대표자명
- `zonecode`: 우편번호
- `address1`: 기본주소
- `address2`: 상세주소

#### Query intent

- 사업장 정보 조회
- 회원의 매장 정보 조회
- 상호명, 대표자명, 주소 조회

#### Filter 기준

- `user_seq`
- `store_seq`

---

### 2.2. view_card_select

등록된 카드 정보를 조회하는 VIEW이다.

한 줄(row)은 등록된 카드 1개를 의미한다.

#### Columns

- `card_seq`: 카드 고유 번호
- `store_seq`: 카드가 연결된 매장 번호
- `card_company`: 카드사명
- `card_name`: 카드 별칭
- `card_number_masked`: 마스킹된 카드 번호
- `is_active`: 카드 사용 여부

#### Query intent

- 등록 카드 목록 조회
- 사용 가능한 카드 조회
- 카드 별칭, 카드사, 마스킹 카드번호 조회

#### Filter 기준

- `store_seq`
- `is_active`

#### Security

- `billing_key`는 포함하지 않는다.
- 전체 카드번호는 포함하지 않는다.

---

### 2.3. view_payment_history

카드 결제 내역을 조회하는 VIEW이다.

한 줄(row)은 결제 1건을 의미한다.

#### Columns

- `payment_seq`: 결제 고유 번호
- `store_seq`: 결제가 발생한 매장 번호
- `partner_seq`: 결제 대상 거래처 번호
- `card_seq`: 결제에 사용된 카드 번호
- `payment_id`: 포트원 결제 고유 ID
- `total_amount`: 총 결제 금액
- `status`: 결제 상태
- `paid_at`: 결제 성공 시간
- `paid_day`: 결제일, `YYYY-MM-DD`
- `paid_month`: 결제 월, `YYYY-MM`
- `failed_reason`: 결제 실패 사유
- `created_at`: 결제 내역 생성일
- `card_company`: 카드사명
- `card_number_masked`: 마스킹된 카드 번호
- `card_type`: 카드 유형
- `card_name`: 카드 별칭
- `is_default`: 대표 카드 여부
- `is_active`: 카드 사용 여부

#### Query intent

- 오늘 결제 내역 조회
- 이번 달 결제 내역 조회
- 결제 성공/실패 내역 조회
- 거래처별 결제 내역 조회
- 카드별 결제 내역 조회

#### Filter 기준

- `store_seq`
- `partner_seq`
- `card_seq`
- `status`
- `paid_day`
- `paid_month`

#### Security

- `billing_key`는 조회하지 않는다.
- 카드번호 전체는 조회하지 않고 `card_number_masked`만 사용한다.

---

### 2.4. view_expense_history

지출 내역을 조회하는 VIEW이다.

한 줄(row)은 지출 1건을 의미한다.

#### Columns

- `expense_seq`: 지출 고유 번호
- `store_seq`: 지출이 발생한 매장 번호
- `category_seq`: 비용 카테고리 번호
- `expense_date`: 지출 일자
- `expense_month`: 지출 월, `YYYY-MM`
- `title`: 지출 내역 제목
- `amount`: 지출 금액
- `memo`: 지출 메모
- `payment_method`: 결제 수단
- `supplier_name`: 구입처명 또는 거래처명
- `ref_type`: 연결 유형. 예: `ORDER`, `GROUP_ORDER`, `DIRECT`
- `ref_seq`: 연결 원본 번호
- `created_at`: 등록일시

#### Query intent

- 지출 내역 조회
- 이번 달 지출 조회
- 직접 구매 내역 조회
- 거래처별 지출 조회
- 결제수단별 지출 조회
- 발주/공동구매/직접구매 지출 구분 조회

#### Filter 기준

- `store_seq`
- `expense_date`
- `expense_month`
- `payment_method`
- `supplier_name`
- `ref_type`

---

### 2.5. view_payment_order_map

결제와 발주 연결 정보를 조회하는 VIEW이다.

한 줄(row)은 결제 1건에 연결된 발주 1건을 의미한다.

#### Columns

- `map_seq`: 결제-발주 연결 고유 번호
- `payment_seq`: 결제 고유 번호
- `order_seq`: 발주 고유 번호
- `amount`: 해당 발주가 결제에 포함된 금액
- `created_at`: 연결 데이터 생성일

#### Query intent

- 특정 결제에 포함된 발주 조회
- 발주가 어떤 결제에 연결되었는지 조회
- 결제 금액이 어떤 발주들로 구성되었는지 조회

#### Filter 기준

- `payment_seq`
- `order_seq`

---

### 2.6. view_current_stock

현재 재고 상태를 조회하는 VIEW이다.

한 줄(row)은 매장에 등록된 재고 품목 1개를 의미한다.

#### Columns

- `stock_seq`: 재고 품목 고유 번호
- `store_seq`: 매장 번호
- `stock_name`: 품목명
- `category`: 카테고리
- `unit`: 수량 단위
- `safety_stock`: 안전 재고 수량
- `default_expiry_days`: 기본 소비기한 일수
- `current_stock`: 현재 총 재고 수량
- `stock_status`: 재고 상태. `LOW` 또는 `NORMAL`
- `created_at`: 품목 최초 등록일시
- `updated_at`: 품목 최종 수정일시

#### Query intent

- 현재 재고 조회
- 부족 재고 조회
- 안전재고 이하 품목 조회
- 품목별 현재 수량 조회

#### Filter 기준

- `store_seq`
- `stock_name`
- `category`
- `stock_status`

---

### 2.7. view_stock_batch_expiration

재고 배치와 유통기한 정보를 조회하는 VIEW이다.

한 줄(row)은 입고 배치 1개를 의미한다.

#### Columns

- `batch_seq`: 재고 배치 고유 번호
- `stock_batches_stock_seq`: 배치가 연결된 재고 품목 번호
- `stock_batches_store_seq`: 배치가 속한 매장 번호
- `lot_number`: 로트 번호
- `detail_stock_name`: 상세 품목명
- `initial_quantity`: 최초 입고 수량
- `current_quantity`: 현재 남은 수량
- `incoming_price`: 입고 단가
- `expiration_date`: 유통기한
- `days_until_expiration`: 유통기한까지 남은 일수
- `incoming_date`: 실제 입고일
- `stock_name`: 품목명
- `category`: 카테고리
- `unit`: 단위

#### Query intent

- 유통기한 조회
- 유통기한 임박 재고 조회
- 배치별 남은 수량 조회
- 로트별 재고 조회
- 입고일 기준 재고 조회

#### Filter 기준

- `stock_batches_store_seq`
- `stock_batches_stock_seq`
- `stock_name`
- `expiration_date`
- `days_until_expiration`

---

### 2.8. view_stock_history

재고 변동 이력을 조회하는 VIEW이다.

한 줄(row)은 재고 변동 이력 1건을 의미한다.

#### Columns

- `history_seq`: 이력 고유 번호
- `store_seq`: 매장 번호
- `stock_seq`: 재고 품목 번호
- `batch_seq`: 재고 배치 번호
- `transaction_type`: 변동 구분
- `change_quantity`: 변동 수량
- `current_total_stock`: 변동 직후 총 재고 수량
- `detail_stock_name`: 상세 품목명
- `price`: 당시 단가
- `expiration_date`: 당시 유통기한
- `reason`: 변동 사유
- `memo`: 추가 메모
- `created_at`: 이력 등록일시
- `stock_name`: 품목명
- `lot_number`: 로트 번호
- `incoming_date`: 입고일

#### Query intent

- 재고 입고 이력 조회
- 재고 출고 이력 조회
- 재고 조정 이력 조회
- 특정 품목의 재고 변동 내역 조회
- 최근 재고 변동 이력 조회

#### Filter 기준

- `store_seq`
- `stock_seq`
- `batch_seq`
- `transaction_type`
- `stock_name`
- `created_at`

---

### 2.9. view_order_summary

발주 요약 정보를 조회하는 VIEW이다.

한 줄(row)은 발주 1건을 의미한다.

#### Columns

- `order_seq`: 발주 고유 번호
- `order_no`: 발주 번호
- `zonecode`: 우편번호
- `address1`: 기본주소
- `address2`: 상세주소
- `order_memo`: 배송 요청 사항
- `buyer_seq`: 발주 신청자 회원 번호
- `seller_seq`: 공급자 회원 번호
- `total_amount`: 발주 총 금액
- `status`: 발주 상태
- `created_at`: 발주 일시
- `item_summary`: 발주 품목 요약

#### Query intent

- 발주 목록 조회
- 최근 발주 조회
- 발주 상태 조회
- 내가 신청한 발주 조회
- 내가 공급자로 받은 발주 조회
- 발주 품목 요약 조회

#### Filter 기준

- `buyer_seq`
- `seller_seq`
- `status`
- `created_at`
- `order_seq`
- `order_no`

---

### 2.10. view_order_detail

발주 상세 품목을 조회하는 VIEW이다.

한 줄(row)은 발주 품목 1개를 의미한다.

#### Columns

- `order_item_seq`: 발주 품목 고유 번호
- `order_seq`: 발주 고유 번호
- `order_no`: 발주 번호
- `buyer_seq`: 발주 신청자 회원 번호
- `seller_seq`: 공급자 회원 번호
- `order_total_amount`: 발주 총 금액
- `order_status`: 발주 상태
- `order_created_at`: 발주 일시
- `item_name`: 품목명
- `category_name`: 카테고리명
- `quantity`: 수량
- `spec`: 규격
- `unit_price`: 단가
- `total_price`: 합계

#### Query intent

- 발주 상세 조회
- 특정 발주에 포함된 품목 조회
- 품목별 발주 수량 조회
- 품목별 단가와 합계 조회

#### Filter 기준

- `order_seq`
- `order_no`
- `buyer_seq`
- `seller_seq`
- `item_name`
- `order_status`

---

### 2.11. view_staff_list

직원 목록을 조회하는 VIEW이다.

한 줄(row)은 직원 1명을 의미한다.

#### Columns

- `employee_seq`: 직원 고유 번호
- `store_seq`: 직원이 소속된 매장 번호
- `emp_name`: 직원 이름
- `work_start_time`: 근무 시작 시간
- `work_end_time`: 근무 종료 시간
- `status`: 현재 근무 상태

#### Query intent

- 직원 목록 조회
- 직원 근무 시간 조회
- 현재 근무 상태 조회

#### Filter 기준

- `store_seq`
- `employee_seq`
- `emp_name`
- `status`

---

### 2.12. view_staff_work_history

직원 근태 이력을 조회하는 VIEW이다.

한 줄(row)은 직원 근태 이력 1건을 의미한다.

#### Columns

- `attendance_seq`: 근태 이력 고유 번호
- `attendance_history_employee_seq`: 직원 고유 번호
- `work_date`: 근무 일자
- `actual_start_time`: 실제 출근 시각
- `actual_end_time`: 실제 퇴근 시각
- `attendance_status`: 근태 상태
- `memo`: 메모
- `store_seq`: 매장 번호
- `emp_name`: 직원 이름
- `work_start_time`: 예정 출근 시간
- `work_end_time`: 예정 퇴근 시간

#### Query intent

- 직원 근태 조회
- 오늘 출근 기록 조회
- 특정 직원의 근무 기록 조회
- 지각/결근/정상 출근 기록 조회

#### Filter 기준

- `store_seq`
- `attendance_history_employee_seq`
- `work_date`
- `emp_name`
- `attendance_status`

---

### 2.13. view_group_buy_history

공동구매 참여 정보를 조회하는 VIEW이다.

한 줄(row)은 공동구매 참여자 1명을 의미한다.

#### Columns

- `participant_seq`: 참여 고유 번호
- `group_buy_seq`: 공동구매 고유 번호
- `participants_user_seq`: 공동구매 참여자 회원 번호
- `payment_status`: 결제 상태
- `delivery_status`: 배송/픽업/수령 상태
- `created_at`: 신청 일시
- `creator_user_seq`: 공동구매 개설자 회원 번호
- `creator_type`: 공동구매 개설자 타입
- `partner_name`: 거래처명
- `item_name`: 품목명
- `category`: 카테고리
- `group_name`: 공동구매 그룹명
- `description`: 공동구매 설명
- `target_participants`: 모집 인원
- `current_participants`: 현재 참여 인원
- `quantity`: 제공 수량
- `unit_price`: 1인당 금액
- `total_amount`: 총 결제 금액
- `end_date`: 모집 마감일
- `pickup_location`: 픽업 주소
- `pickup_time`: 픽업 시간
- `notice`: 공지
- `group_buy_status`: 공동구매 상태

#### Query intent

- 내가 참여한 공동구매 조회
- 공동구매 결제 상태 조회
- 공동구매 배송/픽업 상태 조회
- 공동구매 모집 상태 조회
- 공동구매 품목과 금액 조회

#### Filter 기준

- `participants_user_seq`
- `creator_user_seq`
- `group_buy_seq`
- `payment_status`
- `delivery_status`
- `group_buy_status`
- `item_name`
- `end_date`

---

## 3. View Relationships

VIEW끼리 연결이 필요한 경우 아래 관계만 사용한다.

- `view_payment_history.payment_seq` = `view_payment_order_map.payment_seq`
- `view_payment_order_map.order_seq` = `view_order_summary.order_seq`
- `view_payment_order_map.order_seq` = `view_order_detail.order_seq`
- `view_order_summary.order_seq` = `view_order_detail.order_seq`
- `view_current_stock.stock_seq` = `view_stock_batch_expiration.stock_batches_stock_seq`
- `view_current_stock.stock_seq` = `view_stock_history.stock_seq`
- `view_staff_list.employee_seq` = `view_staff_work_history.attendance_history_employee_seq`

---

## 4. Few-Shot Examples

### 4.1. 사업장 정보 조회

**Q: 내 사업장 정보 알려줘.**

```sql
SELECT
    store_seq,
    company_name,
    ceo_name,
    zonecode,
    address1,
    address2
FROM view_business_info
WHERE user_seq = :userSeq
LIMIT 1;
```

---

### 4.2. 등록 카드 조회

**Q: 등록된 카드 목록 보여줘.**

```sql
SELECT
    card_seq,
    card_company,
    card_name,
    card_number_masked,
    is_active
FROM view_card_select
WHERE store_seq = :storeSeq
  AND is_active = 'Y'
ORDER BY card_seq DESC;
```

---

### 4.3. 오늘 결제 내역 조회

**Q: 오늘 결제 내역 알려줘.**

```sql
SELECT
    payment_seq,
    total_amount,
    status,
    paid_at,
    card_company,
    card_name,
    card_number_masked
FROM view_payment_history
WHERE store_seq = :storeSeq
  AND paid_day = DATE_FORMAT(CURDATE(), '%Y-%m-%d')
ORDER BY paid_at DESC
LIMIT 20;
```

---

### 4.4. 이번 달 결제 내역 조회

**Q: 이번 달 결제 내역 보여줘.**

```sql
SELECT
    payment_seq,
    total_amount,
    status,
    paid_at,
    card_company,
    card_name
FROM view_payment_history
WHERE store_seq = :storeSeq
  AND paid_month = DATE_FORMAT(CURDATE(), '%Y-%m')
ORDER BY paid_at DESC
LIMIT 50;
```

---

### 4.5. 이번 달 지출 내역 조회

**Q: 이번 달 지출 내역 알려줘.**

```sql
SELECT
    expense_seq,
    expense_date,
    title,
    amount,
    payment_method,
    supplier_name,
    ref_type,
    memo
FROM view_expense_history
WHERE store_seq = :storeSeq
  AND expense_month = DATE_FORMAT(CURDATE(), '%Y-%m')
ORDER BY expense_date DESC, expense_seq DESC
LIMIT 50;
```

---

### 4.6. 직접 구매 지출 조회

**Q: 직접 구매 내역 보여줘.**

```sql
SELECT
    expense_seq,
    expense_date,
    title,
    amount,
    payment_method,
    supplier_name,
    memo
FROM view_expense_history
WHERE store_seq = :storeSeq
  AND ref_type = 'DIRECT'
ORDER BY expense_date DESC
LIMIT 50;
```

---

### 4.7. 현재 재고 조회

**Q: 현재 재고 알려줘.**

```sql
SELECT
    stock_seq,
    stock_name,
    category,
    unit,
    current_stock,
    safety_stock,
    stock_status
FROM view_current_stock
WHERE store_seq = :storeSeq
ORDER BY stock_name ASC;
```

---

### 4.8. 부족 재고 조회

**Q: 부족한 재고 알려줘.**

```sql
SELECT
    stock_seq,
    stock_name,
    current_stock,
    safety_stock,
    unit,
    stock_status
FROM view_current_stock
WHERE store_seq = :storeSeq
  AND stock_status = 'LOW'
ORDER BY stock_name ASC;
```

---

### 4.9. 유통기한 임박 재고 조회

**Q: 유통기한 임박한 재고 알려줘.**

```sql
SELECT
    batch_seq,
    stock_name,
    detail_stock_name,
    current_quantity,
    unit,
    expiration_date,
    days_until_expiration
FROM view_stock_batch_expiration
WHERE stock_batches_store_seq = :storeSeq
  AND days_until_expiration BETWEEN 0 AND 7
ORDER BY expiration_date ASC
LIMIT 50;
```

---

### 4.10. 재고 변동 이력 조회

**Q: 최근 재고 변동 이력 알려줘.**

```sql
SELECT
    history_seq,
    stock_name,
    detail_stock_name,
    transaction_type,
    change_quantity,
    current_total_stock,
    reason,
    memo,
    created_at
FROM view_stock_history
WHERE store_seq = :storeSeq
ORDER BY created_at DESC
LIMIT 30;
```

---

### 4.11. 발주 요약 조회

**Q: 내 최근 발주 내역 알려줘.**

```sql
SELECT
    order_seq,
    order_no,
    total_amount,
    status,
    created_at,
    item_summary
FROM view_order_summary
WHERE buyer_seq = :userSeq
ORDER BY created_at DESC
LIMIT 20;
```

---

### 4.12. 발주 상세 조회

**Q: 이 발주에 어떤 품목이 들어갔어?**

```sql
SELECT
    order_item_seq,
    order_seq,
    order_no,
    item_name,
    category_name,
    quantity,
    spec,
    unit_price,
    total_price
FROM view_order_detail
WHERE order_seq = :orderSeq
ORDER BY order_item_seq ASC;
```

---

### 4.13. 직원 목록 조회

**Q: 직원 목록 보여줘.**

```sql
SELECT
    employee_seq,
    emp_name,
    work_start_time,
    work_end_time,
    status
FROM view_staff_list
WHERE store_seq = :storeSeq
ORDER BY emp_name ASC;
```

---

### 4.14. 직원 근태 조회

**Q: 오늘 직원 근태 알려줘.**

```sql
SELECT
    attendance_seq,
    emp_name,
    work_date,
    actual_start_time,
    actual_end_time,
    attendance_status,
    memo
FROM view_staff_work_history
WHERE store_seq = :storeSeq
  AND work_date = CURDATE()
ORDER BY emp_name ASC;
```

---

### 4.15. 공동구매 참여 정보 조회

**Q: 내가 참여한 공동구매 알려줘.**

```sql
SELECT
    participant_seq,
    group_buy_seq,
    group_name,
    item_name,
    quantity,
    unit_price,
    payment_status,
    delivery_status,
    group_buy_status,
    end_date,
    pickup_location,
    pickup_time
FROM view_group_buy_history
WHERE participants_user_seq = :userSeq
ORDER BY created_at DESC
LIMIT 30;
```

---

### 4.16. 결제에 연결된 발주 조회

**Q: 이 결제에 어떤 발주가 포함됐어?**

```sql
SELECT
    pom.payment_seq,
    pom.order_seq,
    pom.amount,
    os.order_no,
    os.item_summary,
    os.total_amount,
    os.status
FROM view_payment_order_map pom
JOIN view_order_summary os
    ON pom.order_seq = os.order_seq
WHERE pom.payment_seq = :paymentSeq
ORDER BY pom.created_at DESC;
```

---

## 5. Response Formatting Guidance

조회 결과를 사용자에게 설명할 때는 다음 기준을 따른다.

- 금액은 원 단위로 자연스럽게 표현한다.
- 재고 수량은 `unit`과 함께 표현한다.
- 날짜는 사용자가 이해하기 쉬운 형식으로 표현한다.
- 결제 상태, 발주 상태, 재고 상태는 원문 값을 유지하되 필요하면 간단히 해석한다.
- 조회 결과가 없으면 “해당 조건의 데이터가 없습니다.”라고 답한다.
- 데이터 수정이 필요한 요청은 직접 실행하지 않고, 조회만 가능하다고 안내한다.
