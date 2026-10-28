/*******************************************************************************
 * Copyright (C) 2026 WaveMaker, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wavemaker.commons.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONUtilsFixture {

    public enum Kind {
        PLAIN,
        CUSTOM_TO_STRING {
            @Override
            public String toString() {
                return "custom-to-string";
            }
        }
    }

    public static class Child {

        private String childName;

        private Integer count;

        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    private String name;

    private int number;

    private long bigNumber;

    private double ratio;

    private BigDecimal amount;

    private boolean active;

    private Boolean nullableFlag;

    private String nullText;

    private String URL;

    private String ID;

    private String XMLData;

    private Kind plainKind;

    private Kind customKind;

    private Date createdOn;

    private java.sql.Date businessDate;

    private byte[] payload;

    private List<String> tags;

    private Map<String, Object> attributes = new LinkedHashMap<>();

    private Child child;

    private Object headers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getBigNumber() {
        return bigNumber;
    }

    public void setBigNumber(long bigNumber) {
        this.bigNumber = bigNumber;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Boolean getNullableFlag() {
        return nullableFlag;
    }

    public void setNullableFlag(Boolean nullableFlag) {
        this.nullableFlag = nullableFlag;
    }

    public String getNullText() {
        return nullText;
    }

    public void setNullText(String nullText) {
        this.nullText = nullText;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getXMLData() {
        return XMLData;
    }

    public void setXMLData(String XMLData) {
        this.XMLData = XMLData;
    }

    public Kind getPlainKind() {
        return plainKind;
    }

    public void setPlainKind(Kind plainKind) {
        this.plainKind = plainKind;
    }

    public Kind getCustomKind() {
        return customKind;
    }

    public void setCustomKind(Kind customKind) {
        this.customKind = customKind;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public java.sql.Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(java.sql.Date businessDate) {
        this.businessDate = businessDate;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    public static JSONUtilsFixture sample(Object headers) {
        JSONUtilsFixture fixture = new JSONUtilsFixture();
        fixture.setName("sample");
        fixture.setNumber(42);
        fixture.setBigNumber(9007199254740993L);
        fixture.setRatio(0.5);
        fixture.setAmount(new BigDecimal("1234.50"));
        fixture.setActive(true);
        fixture.setURL("https://example.com");
        fixture.setID("id-1");
        fixture.setXMLData("<a/>");
        fixture.setPlainKind(Kind.PLAIN);
        fixture.setCustomKind(Kind.CUSTOM_TO_STRING);
        fixture.setCreatedOn(new Date(1700000000000L));
        fixture.setBusinessDate(java.sql.Date.valueOf("2026-10-28"));
        fixture.setPayload(new byte[]{1, 2, 3});
        fixture.setTags(List.of("a", "b"));
        fixture.getAttributes().put("zeta", 1);
        fixture.getAttributes().put("alpha", List.of(1, 2));
        Child child = new Child();
        child.setChildName("kid");
        fixture.setChild(child);
        fixture.setHeaders(headers);
        return fixture;
    }
}
