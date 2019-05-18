package com.example.vanhieu.htmusic;

/**
 * Created by VanHieu on 16/12/2015.
 */
public class Utilities {

    /**
     * Hàm chuyển đổi milliseconds  đến
     * định dạng Timer
     * Giờ:Phút:Giây
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Thêm giờ nếu có
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Thêm 0 nếu như giây có thêm một chữ số
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    /**
     * Hàm tính % progress
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // tính phần trăm
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        return percentage.intValue();
    }

    /**
     * Hàm thay đổi progress đến timer
     * @param progress -
     * @param totalDuration
     * trả về giây hiện hành đang chơi
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }
}
