package com.example.bricklist1.dbhandler

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.example.bricklist1.bitmapByteDownloader
import com.example.bricklist1.model.Part
import com.example.bricklist1.model.project
import com.example.bricklist1.projectList
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class dbManager(c: Context){
    val db = dbOpener(c,null,"BrickList.db")
    val context = c
    fun close(){
        db.close()
    }
    private fun isTherePart(code:String): Boolean {
        val idC = db.rawQuery("SELECT id FROM Parts WHERE code=?",Array<String>(1){code})
        idC.moveToFirst()
        val res = (idC.count>0)
        idC.close()
        return res
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
    private fun getIDpart(code:String):Int{
        val sel = Array<String>(1) {code}
        val idC = db.rawQuery("SELECT p.id FROM Parts p WHERE p.id=?",sel)
        idC.moveToFirst()
        val num = idC.count
        if (num==0) return -1
        var res = idC.getInt(0)
        if (num==0) res =-1
        idC.close()
        return res
    }

    private fun insertImage(partID:Int, colorID:Int, image:ByteArray){
        val con = ContentValues()
        con.put("Image",image)
        val sel = Array<String>(2) { partID.toString() }
        sel[1] = colorID.toString()
        db.update("Codes",con,"itemId =? AND colorID=?",sel)
    }
    private fun getImageFromCodeAndColorCode(code:String, colorCode:Int):ByteArray?{
        val partID = getIDpart(code)
        val sel = Array<String>(2) { partID.toString() }
        sel[1] = colorCode.toString()
        val cu = db.rawQuery("SELECT c.Image FROM Codes c WHERE ItemID=? AND colorID=?",sel)
        val num = cu.count
        var res:ByteArray? = null
        if (num!=0) res=cu.getBlob(0)
        cu.close()
        return res
    }
    private fun getImageFromCodeAndColorCode(code:String, colorCode:Int, typeCode:String):ByteArray?{
        var res = getImageFromCodeAndColorCode(code,colorCode)
        if (res == null) {
            val bmp = bitmapByteDownloader()
            val ex = bmp.execute(typeCode,code,colorCode.toString())
            res = ex.get(5000,TimeUnit.MILLISECONDS)
            if ((res != null)) {
                insertImage(getIDpart(code),getColorIDFromCode(colorCode),res)
            }
        }
        return res
    }

    fun getPartFromXML(code:String,color: Int,itemtype:String,required:Int,extra:Int): Part?{
        if (!isTherePart(code)) {
            insertPart(code,itemtype,color)
        }
        val sel = Array<String>(2){""}
        sel[0]=code
        sel[1]=color.toString()
        var cu= db.rawQuery(
            "SELECT p.id, p.Name, col.Name, it.id " +
                "FROM Parts p, Colors col, ItemTypes it " +
                "WHERE p.code = ? " +
                    "AND col.code = ? " +
                    "AND p.TypeID=it.id",
            sel)
        cu.moveToFirst()
        if(cu.count==0) {
            cu.close()
            insertCode(getIDpart(code),color)
            cu = db.rawQuery(
                "SELECT p.id, p.Name, col.Name, it.id " +
                        "FROM Parts p, Colors col,ItemTypes it " +
                        "WHERE p.code = ? " +
                        "AND col.code = ? " +
                        "AND p.TypeID=it.id",sel)
            cu.moveToFirst()
        }
        val image = getImageFromCodeAndColorCode(code,color,itemtype)
        var res:Part? = null
            res = Part()
            res.code = code
            res.colorID = getColorIDFromCode(color)
            res.itemtype = itemtype
            res.required = required
            res.extra = extra
            res.bitmap = BitmapFactory.decodeByteArray(image,0,0)
            res.found = 0
            res.name = cu.getString(1)
            res.itemID = cu.getInt(0)
            res.color = cu.getString(2)
            res.typeID = cu.getInt(3)
        cu.close()
        return res
    }
    private fun getColorIDFromCode(Code:Int):Int{
        var res:Int=1
        val sel = Array<String>(1){Code.toString()}
        val cuIP = db.rawQuery("SELECT it.id from Colors it " +
                "WHERE it.Code =?",sel)
        cuIP.moveToFirst()
        try{
            res = cuIP.getInt(0)
        }catch (e:Exception){
            e.printStackTrace()
        }
        cuIP.close()
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
        db.insert("InventoriesParts",null,values)
    }
    private fun getMaxIDproject(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM INVENTORIES",null)
        idC.moveToFirst()
        val res = idC.getInt(0)
        idC.close()
        return res
    }

    private fun getMaxIDPart(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM Parts",null)
        idC.moveToFirst()
        val res = idC.getInt(0)
        idC.close()
        return res
    }
    private fun getMaxIDCode(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM Codes",null)
        idC.moveToFirst()
        val res = idC.getInt(0)
        idC.close()
        return res
    }
    private fun getMaxIDProjectsPart(): Int {
        val idC = db.rawQuery("SELECT MAX(id) FROM INVENTORIESPARTS",null)
        idC.moveToFirst()
        val res = if (idC.count==0) -1 else idC.getInt(0)
        idC.close()
        return res
    }

    private fun fillPartFromIDs(part: Part){
        val sel = Array<String>(2){""}
        sel[0]=part.itemID.toString()
        sel[1]=part.colorID.toString()
        val cu= db.rawQuery(
            "SELECT p.code,col.Name,p.Name,col.Code " +
                    "FROM Parts p, Colors col " +
                    "WHERE p.id=? " +
                    "AND col.id =? ",
            sel)
        cu.moveToFirst()
        part.code = cu.getString(0)
        part.color = cu.getString(1)
        part.name = cu.getString(2)
        part.itemtype = getTypeFromID(part.typeID)
        val col =cu.getInt(3)
        cu.close()
    }

    private fun loadPartsToProject(proj:project){
        val sel = Array<String>(1){proj.idProject.toString()}
        val cuIP = db.rawQuery("SELECT * from InventoriesParts ip " +
                "WHERE ip.InventoryID =?",sel)
        cuIP.moveToFirst()
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
    fun loadProjects(pl:ArrayList<project>){
        val cu = db.rawQuery("SELECT id,name,active from inventories ORDER BY LastAccessed DESC",null)
        cu.moveToFirst()
        val n= cu.count
        var res:project? = null
        var cond = cu.count>0
        while(cond) {
            res = project(cu.getString(1))
            res.idProject = cu.getInt(0)
            res.active = cu.getInt(2)
            loadPartsToProject(res)
            Log.i(res.name,"Added")
            pl.add(res)
            cond=cu.moveToNext()
        }
        cu.close()
    }
    fun updatePart(invPartID:Int,found:Int){
        val cVal = ContentValues()
        cVal.put("QuantityInStore",found)
        db.update("InventoriesParts",cVal,"id=?",Array(1){invPartID.toString()})
    }
    private fun getTypeIDFromType(itemType:String): Int {
        val sel = Array<String>(1){itemType}
        val cuIP = db.rawQuery("SELECT id from ItemTypes it " +
                "WHERE it.Code =?",sel)
        cuIP.moveToFirst()
        var res = -1
        try{
            res = cuIP.getInt(0)
        }catch (e:Exception){
            e.printStackTrace()
        }
        cuIP.close()
        return res
    }
    private fun getTypeFromID(typeID:Int): String {
        val sel = Array(1){typeID.toString()}
        Log.i("$sel[0] getting type",typeID.toString())
        val cuIP = db.rawQuery("SELECT Code from ItemTypes WHERE id = ?",sel)
        cuIP.moveToFirst()
        var res = "P"
        res = cuIP.getString(0)
        cuIP.close()
        return res
    }
    private fun insertCode(idPart:Int,ColorCode:Int){
        val cVal = ContentValues()
        val idCo = getMaxIDCode()+1
        cVal.put("id",idCo)
        cVal.put("itemID",idPart)
        cVal.put("ColorID",getColorIDFromCode(ColorCode))
        Log.i("sd","Inserted new Code ${cVal.get("ColorID")}")
        db.insert("Codes",null, cVal)
    }
    fun insertPart(code:String,itemType:String,colorCode: Int){
        Log.i("sd","Inserted new part")
        val idPart = getMaxIDPart()+1
        val cVal = ContentValues()
        cVal.put("id",idPart)
        cVal.put("TypeID",getTypeIDFromType(itemType))
        cVal.put("code",code)
        cVal.put("name","PartOutOfDB")
        cVal.put("namePL","nwm")
        cVal.put("CategoryID",1)
        db.insert("Parts",null,cVal)
        insertCode(idPart,colorCode)
    }

    private fun getColorCodeFromID(ID:Int):Int{
        var res:Int=1
        val sel = Array<String>(1){ID.toString()}
        val cuIP = db.rawQuery("SELECT it.Code from Colors it " +
                "WHERE it.id =?",sel)
        cuIP.moveToFirst()
        try{
            res = cuIP.getInt(0)
        }catch (e:Exception){
            e.printStackTrace()
        }
        cuIP.close()
        return res
    }

    fun loadImages(p:project){
        for (part in p.partlist){
            val col = getColorCodeFromID(part.colorID!!)
            val im = getImageFromCodeAndColorCode(part.code,col,part.itemtype)
            if (im!=null) part.bitmap = BitmapFactory.decodeByteArray(im,0,im.size)
        }
    }
}