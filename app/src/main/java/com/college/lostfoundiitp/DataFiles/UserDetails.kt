package com.college.lostfoundiitp.DataFiles


data class UserDetails(val Name: String = "", val Email: String ="", val Phone: String ="", val Rollno: String ="") {

    private fun getname(): String {
        return Name
    }

    private fun getemail(): String {
        return Email
    }

    private fun getphone(): String {
        return Phone
    }

    private fun getroll(): String {
        return Rollno
    }

}
