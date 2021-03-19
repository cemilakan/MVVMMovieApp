package net.kisacasi.myarchive.models

import java.io.Serializable


class GenrePlus :Serializable{
    var catId: Int
    var catImageInt: Int

    constructor(catId: Int, catImageInt: Int) {
        this.catId = catId
        this.catImageInt = catImageInt
    }

}