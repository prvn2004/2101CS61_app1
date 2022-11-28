package com.college.lostfoundiitp.DataFiles

import com.google.firebase.firestore.DocumentId

data class AdsDataFile(
    var AdType: String = "",
    var AdEmail: String ="",
    var AdImgUri: HashMap<String, String> = HashMap(),
    var AdPhone: String = "",
    var AdName: String = "",
    var AdRollno: String = "",
    var AdMessage: String ="",
    var AdUid: String = "",
    var AdDescription: String = "",
    var AdId: String = "",
    var AdLocation: String = "",
    var AdTimestamp: String = "",
    var AdTime: String = "",
    var AdDate: String = "",

    @DocumentId
    val documentId: String = ""
) {
    fun getImg(): HashMap<String, String>{
        return AdImgUri
    }

    fun getId(): String{
        return documentId
    }

    fun getemail(): String{
        return AdEmail
    }

    fun getLocation(): String{
        return AdLocation
    }

    fun getRollno(): String {
        return AdRollno
    }

    fun getphone(): String {
        return AdPhone
    }

    fun gettype(): String{
        return AdType
    }

    fun getName(): String {
        return AdName
    }

    fun getUid(): String {
        return AdUid
    }

    fun getTime(): String {
        return AdTime
    }

    fun getDescription(): String {
        return AdDescription
    }

    fun getDate(): String {
        return AdDate
    }
}
