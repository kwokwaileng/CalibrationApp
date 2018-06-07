package com.coe.clockwaiz

import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Utility {


    @Throws(IOException::class)
    fun writeToCsvInput(context: Context,position: String,csvString:String) {
        val chars = arrayOf("|", "\\", "?", "*", "<", "\"", ":", ">", "+", " ", "?", ".", "-", "#", "@", "$", "%", "&", "/", ",", "|")
        var filePath: String? ="${getDateTime()}_${position}_${Build.MODEL}_accel_norm_input"
        for (c in chars) {
            filePath = filePath?.replace(c, "")
        }
        filePath += ".csv"
        val folder = File(context.getExternalFilesDir("CsvFiles"), filePath)


        if (!folder.exists())
            folder.createNewFile()


        val filewriter = FileWriter(folder, true)
        filewriter.append(csvString)
        filewriter.close()

    }

    @Throws(IOException::class)
    fun writeToCsvSummary(context: Context,position: String,csvString:String) {

        val chars = arrayOf("|", "\\", "?", "*", "<", "\"", ":", ">", "+", " ", "?", ".", "-", "#", "@", "$", "%", "&", "/", ",", "|")
        var filePath: String? ="${getDateTime()}_${position}_${Build.MODEL}_accel_norm_summary"
        for (c in chars) {
            filePath = filePath?.replace(c, "")
        }
        filePath += ".csv"
        val folder = File(context.getExternalFilesDir("CsvFiles"), filePath)


        if (!folder.exists())
            folder.createNewFile()


        val filewriter = FileWriter(folder, true)
        filewriter.append(csvString)
        filewriter.close()

    }


    fun getDateTime():String{
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time)
    }


    @Throws(IOException::class)
    fun writeToCsvTrip(context: Context,csvString:String) {
        val chars = arrayOf("|", "\\", "?", "*", "<", "\"", ":", ">", "+", " ", "?", ".", "-", "#", "@", "$", "%", "&", "/", ",", "|")
        var filePath: String? ="${getDateTime()}_${Build.MODEL}_trip_input"
        for (c in chars) {
            filePath = filePath?.replace(c, "")
        }
        filePath += ".csv"
        val folder = File(context.getExternalFilesDir("CsvFiles"), filePath)


        if (!folder.exists())
            folder.createNewFile()


        val filewriter = FileWriter(folder, true)
        filewriter.append(csvString)
        filewriter.close()

    }



}