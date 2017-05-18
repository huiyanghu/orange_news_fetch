   
  
  
  
  

package com.cki.spider.pro;

import java.io.Serializable;

   
  
  
public final class MimeType implements Serializable {

    private static final long serialVersionUID = 701250931389172701L;

       
  
  
    public static class Major implements Serializable {

        private static final long serialVersionUID = -1903909895450636785L;

        public static final Major TEXT = new Major("text");

        public static final Major APPLICATION = new Major("application");

        private final String major;

        private Major(String major) {
            this.major = major;
        }

        public static Major valueOf(String major) {

            major = major.toLowerCase();

            return new Major(major);
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Major) {
                return this.major.equals(((Major) obj).major);
            }

            return false;
        }

        @Override
        public String toString() {
            return this.major;
        }
    }


       
  
  
    public static final class Minor implements Serializable {

        private static final long serialVersionUID = -6433810295529204157L;
        public static final Minor PLAIN = new Minor("plain");
        public static final Minor HTML = new Minor("html");
        public static final Minor XHTML = new Minor("xhtml");
        public static final Minor XML = new Minor("xml");
        public static final Minor CSS = new Minor("css");
        public static final Minor JAVASCRIPT = new Minor("javascript");
        public static final Minor OCTET_STREAM = new Minor("octet-stream");
        public static final Minor UNKOWN = new Minor("unkown");

        private final String minor;

        private Minor(String minor) {
            this.minor = minor;
        }

        public static Minor valueOf(String minor) {

            minor = minor.toLowerCase();

            return new Minor(minor);
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Minor) {
                return this.minor.equals(((Minor) obj).minor);
            }

            return false;
        }

        @Override
        public String toString() {
            return this.minor;
        }
    }


    private Major major;
    private Minor minor;

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public Minor getMinor() {
        return minor;
    }

    public void setMinor(Minor minor) {
        this.minor = minor;
    }

    public MimeType() {

    }

    public MimeType(Major major, Minor minor) {

        this.major = major;
        this.minor = minor;
    }

    public MimeType(String mimeType) {

        if (mimeType == null || mimeType.equals("")) {
            return;
        }

        String[] segs = mimeType.split("/");

        this.major = MimeType.Major.valueOf(segs[0]);

        if (segs.length > 1) {
            this.minor = MimeType.Minor.valueOf(segs[1]);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof MimeType) {
            MimeType _obj = (MimeType) obj;

            return (this.minor != null ? this.minor.equals(_obj.minor) : this.minor == _obj.minor)
                   && (this.major != null ? this.major.equals(_obj.major) : this.major == _obj.major);
        }

        return false;
    }

    public String toString() {
        return this.major.major + "/" + this.minor.minor;
    }
}
