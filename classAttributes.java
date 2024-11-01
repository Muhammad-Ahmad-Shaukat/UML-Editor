package com.boota.demo;

public class classAttributes {
    String attributename;
    String attributetype;
    String datatype;

    public classAttributes(String attributename, String attributetype, String datatype) {
        this.attributename = attributename;
        this.attributetype = attributetype;
        this.datatype = datatype;
    }

    public String getAttributename() {
        return attributename;
    }

    public void setAttributename(String attributename) {
        this.attributename = attributename;
    }

    public String getAttributetype() {
        return attributetype;
    }

    public void setAttributetype(String attributetype) {
        this.attributetype = attributetype;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    @Override
    public String toString() {
        if (attributetype=="Private") {
        return "- "+attributename + " : "+ datatype;}
        else{
            return "+ "+attributename + " : "+ datatype;
        }
    }
}
