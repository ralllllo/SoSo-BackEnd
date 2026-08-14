package com.soso.domain.member.dto;

import java.util.List;


public class BizValidateDto {

    
    public static class Request {
        private List<BizInfo> businesses;

        public Request() {}
        public Request(List<BizInfo> businesses) { this.businesses = businesses; }
        public List<BizInfo> getBusinesses() { return businesses; }
        public void setBusinesses(List<BizInfo> businesses) { this.businesses = businesses; }

        public static class BizInfo {
            private String b_no;
            private String start_dt;
            private String p_nm;
            private String b_nm;
            private String p_nm2;
            private String corp_no;
            private String b_adr;

            public BizInfo() {}
            public BizInfo(String b_no, String start_dt, String p_nm, String b_nm, String p_nm2, String corp_no, String b_adr) {
                this.b_no = b_no; this.start_dt = start_dt; this.p_nm = p_nm; this.b_nm = b_nm;
                this.p_nm2 = p_nm2; this.corp_no = corp_no; this.b_adr = b_adr;
            }
            
            public String getB_no() { return b_no; }
            public void setB_no(String b_no) { this.b_no = b_no; }
            public String getStart_dt() { return start_dt; }
            public void setStart_dt(String start_dt) { this.start_dt = start_dt; }
            public String getP_nm() { return p_nm; }
            public void setP_nm(String p_nm) { this.p_nm = p_nm; }
            public String getB_nm() { return b_nm; }
            public void setB_nm(String b_nm) { this.b_nm = b_nm; }
            public String getP_nm2() { return p_nm2; }
            public void setP_nm2(String p_nm2) { this.p_nm2 = p_nm2; }
            public String getCorp_no() { return corp_no; }
            public void setCorp_no(String corp_no) { this.corp_no = corp_no; }
            public String getB_adr() { return b_adr; }
            public void setB_adr(String b_adr) { this.b_adr = b_adr; }
        }
    }

    
    public static class Response {
        private Integer request_cnt;
        private Integer valid_cnt;
        private String status_code;
        private List<BizData> data;

        public Response() {}
        public Response(Integer request_cnt, Integer valid_cnt, String status_code, List<BizData> data) {
            this.request_cnt = request_cnt; this.valid_cnt = valid_cnt;
            this.status_code = status_code; this.data = data;
        }
        
        public Integer getRequest_cnt() { return request_cnt; }
        public void setRequest_cnt(Integer request_cnt) { this.request_cnt = request_cnt; }
        public Integer getValid_cnt() { return valid_cnt; }
        public void setValid_cnt(Integer valid_cnt) { this.valid_cnt = valid_cnt; }
        public String getStatus_code() { return status_code; }
        public void setStatus_code(String status_code) { this.status_code = status_code; }
        public List<BizData> getData() { return data; }
        public void setData(List<BizData> data) { this.data = data; }

        public static class BizData {
            private String b_no;
            private String valid;      
            private String valid_msg;

            public BizData() {}
            public BizData(String b_no, String valid, String valid_msg) {
                this.b_no = b_no; this.valid = valid; this.valid_msg = valid_msg;
            }
            
            public String getB_no() { return b_no; }
            public void setB_no(String b_no) { this.b_no = b_no; }
            public String getValid() { return valid; }
            public void setValid(String valid) { this.valid = valid; }
            public String getValid_msg() { return valid_msg; }
            public void setValid_msg(String valid_msg) { this.valid_msg = valid_msg; }
        }
    }
}