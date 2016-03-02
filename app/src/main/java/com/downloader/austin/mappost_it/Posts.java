package com.downloader.austin.mappost_it;

/**
 * Created by Austin on 30/9/2015.
 */
public class Posts {

        private long id;
        private double longitude,latitude;
        private String content;
        private boolean enabled;


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        // Will be used by the ArrayAdapter in the ListView
        @Override
        public String toString() {
            return content;
        }

}
