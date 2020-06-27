package com.example.bricklist1.dbhandler

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import com.example.bricklist1.bitmapByteDownloader
import com.example.bricklist1.model.Part
import com.example.bricklist1.model.project
import com.example.bricklist1.projectList
import java.util.*

class dbManager(c: Context){
    val db = dbOpener(c,null,"BrickList.db")
    val context = c
    fun close(){
        db.close()
    }
    fun updateLastAccess(idInventory: Int){
        val la = Date().time
        val cval = ContentValues()
        cval.put("LastAccessed",la)
        db.update("Inventories",cval,"id=?",Array<String>(1){idInventory.toString()})
    }
    fun insertNewInventory(p:project){
        val la = Date().time
        val cval = ContentValues()
        val idInv = getMaxIDproject()+1
        cval.put("LastAccessed",la)
        cval.put("id",idInv)
        cval.put("Name",p.name)
        cval.put("Active",p.active)
        db.insert("Inventories",null,cval)
        for (part in p.partlist){
            putPartToInventory(idInv,part)
        }
    }
    fun getIDpart(code:String):Int{
        val col = Array<String>(1) {"id"}
        val sel = Array<String>(1) {code}
        val idC = db.query("parts",col,"id=(?)",sel,null,null,null)
        val res = if (idC.count==0) -1 else idC.getInt(0)
        idC.close()
        return res
    }

    private fun insertImage(partID:Int, colorID:Int, image:ByteArray){
        val con = ContentValues()
        con.put("Image",image)
        val sel = Array<String>(2) { partID.toString() }
        sel[1] = colorID.toString()
        db.update("Codes",con,"partId =? AND colorID=?",sel)
    }
    private fun getImageFromCodeAndColorID(code:String, colorID:Int):ByteArray?{
        val partID = getIDpart(code)
        val col = Array<String>(1) { "Image" }
        val sel = Array<String>(2) { partID.toString() }
        sel[1] = colorID.toString()
        val cu = db.query("Codes",col,"partID=? AND colorID=?",sel,null,null,null)
        val res = if (cu.isNull(0)) null
            else cu.getBlob(0)
        cu.close()
        return res
    }
    private fun getImageFromCodeAndColorID(code:String, colorID:Int, typeCode:String):ByteArray?{
        var res = getImageFromCodeAndColorID(code,colorID)
        if (res!=null){
            val bmd = bitmapByteDownloader()
            val ex = bmd.execute()
            res = bmd.get()
            (if ((res != null)) {
                insertImage(getIDpart(code),colorID,res)
            })
        }
        return res
    }

    fun getPartFromXML(code:String,color: Int,itemtype:String,required:Int,extra:Int): Part?{
        val sel = Array<String>(2){""}
        sel[0]=code
        sel[1]=color.toString()
        val cu= db.rawQuery(
            "SELECT p.id,p.Name,col.Name,cod.Image, p.Code, it.Name,it.id" +
                "FROM Parts p, Colors col, Codes cod,ItemTypes it " +
                "WHERE p.code=? " +
                    "AND col.code =? " +
                    "AND p.TypeID=it.id " +
                    "AND cod.itemID=p.id " +
                    "AND cod.ColorID=col.Code",
            sel)
        if (cu.count == 0) return null
        val image = getImageFromCodeAndColorID(code,color,itemtype)
        var res:Part? = null
        if (image!=null){
            res = Part()
            res.code = code
            res.colorID = color
            res.itemtype = itemtype
            res.required = required
            res.extra = extra
            res.bitmap = BitmapFactory.decodeByteArray(image,0,image.size)
            res.found = 0
            res.name = cu.getString(1)
            res.itemID = cu.getInt(0)
            res.color = cu.getString(2)
            res.typeID = cu.getInt(6)
        }
        cu.close()
        return res
    }

    private fun putPartToInventory(idInventory:Int, p:Part){
        val values = ContentValues()
        val idIP = getMaxIDProjectsPart()+1
        values.put("id",idIP)
        values.put("InventoryID",idInventory)
        values.put("ColorID",p.colorID)
        values.put("QuantityInSet",p.required)
        values.put("QuantityInStore",p.found)
        values.put("Extra",p.extra)
        values.put("ItemID",p.itemID)
        values.put("TypeID",p.typeID)
        p.id = idIP
        db.insert("InventoriesPart",null,values)
    }
    private fun getMaxIDproject(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM INVENTORIES",null)
        val res = if (idC.count==0) -1 else idC.getInt(0)
        idC.close()
        return res
    }

    private fun getMaxIDProjectsPart(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM INVENTORIESPARTS",null)
        val res = if (idC.count==0) -1 else idC.getInt(0)
        idC.close()
        return res
    }

    private fun fillPartFromIDs(part: Part){
        val sel = Array<String>(3){""}
        sel[0]=part.itemID.toString()
        sel[1]=part.colorID.toString()
        sel[2]=part.itemID.toString()
        val cu= db.rawQuery(
            "SELECT p.code,col.Name,it.Code,p.Name" +
                    "FROM Parts p, Colors col, Codes cod, ItemTypes it" +
                    "WHERE p.id=? " +
                    "AND col.id =? " +
                    "AND p.TypeID=?" +
                    "AND p.TypeID=it.id" +
                    "AND cod.itemID=p.id " +
                    "AND cod.ColorID=col.Code",
            sel)
        part.code = cu.getString(0)
        part.color = cu.getString(1)
        part.itemtype = cu.getString(2)
        val im = getImageFromCodeAndColorID(part.code,part.colorID,part.itemtype)
        if (im!=null) part.bitmap = BitmapFactory.decodeByteArray(im,0,im.size)
        part.name = cu.getString(3)
        cu.close()
    }

    private fun loadPartsToProject(proj:project){
        val sel = Array<String>(1){proj.idProject.toString()}
        val cuIP = db.rawQuery("SELECT * from InventoriesParts ip" +
                "WHERE ip.InventoryID = AND ? AND",sel)
        val n= cuIP.count
        var res:Part? = null
        var cond = (cuIP.count>0)
        while(cond) {
            res = Part()
            res.typeID = cuIP.getInt(cuIP.getColumnIndex("TypeID"))
            res.id = cuIP.getInt(cuIP.getColumnIndex("id"))
            res.colorID = cuIP.getInt(cuIP.getColumnIndex("ColorID"))
            res.found = cuIP.getInt(cuIP.getColumnIndex("QuantityInStore"))
            res.required = cuIP.getInt(cuIP.getColumnIndex("QuantityInSet"))
            res.itemID = cuIP.getInt(cuIP.getColumnIndex("ItemID"))
            res.extra = cuIP.getInt(cuIP.getColumnIndex("Extra"))
            fillPartFromIDs(res)
            proj.partlist.add(res)
            cond = cuIP.moveToNext()
        }
        cuIP.close()
    }
    fun loadProjects(){
        val cu = db.rawQuery("SELECT * from inventories",null)
        val n= cu.count
        var res:project? = null
        var cond = if (cu.count>0) true else false
        while(cond) {
            res = project(cu.getString(cu.getColumnIndex("name")))
            res.idProject = cu.getInt(cu.getColumnIndex("id"))
            res.active = cu.getInt(cu.getColumnIndex("active"))
            loadPartsToProject(res)
            projectList.add(res)
            cond=cu.moveToNext()
        }
        cu.close()
    }
}