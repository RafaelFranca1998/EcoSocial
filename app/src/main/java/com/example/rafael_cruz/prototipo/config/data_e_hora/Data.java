/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.config.data_e_hora;

public class Data {

	public Data() {
	}
	
	public static String dateToString(int yy, int mm, int dd) {
        int year_x  = yy;
        int month_x = mm;
        int day_x   = dd;
        String parse;
        if ( month_x < 10 && day_x > 9 ){
            parse = day_x+"0"+month_x +""+year_x;
        }else if ( day_x < 10 && month_x < 10 ){
            parse = "0"+day_x+"0"+month_x +""+year_x;
        }else if (day_x < 10&&month_x>9){
            parse = "0"+day_x+""+month_x +""+year_x;
        }else {
            parse = day_x+""+month_x +""+year_x;
        }
        String dataString = String.valueOf(parse);
        System.out.println("dia"+String.valueOf(day_x));
        System.out.println("mes"+String.valueOf(month_x));
        System.out.println("ano"+String.valueOf(year_x));
        return (String.valueOf(dataString));
    }
	
}
