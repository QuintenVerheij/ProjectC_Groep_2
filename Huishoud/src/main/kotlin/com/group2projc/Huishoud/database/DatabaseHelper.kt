package com.group2projc.Huishoud.database

import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.authorid
import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.date
import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.groupid
import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.mutation
import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.product
import com.group2projc.Huishoud.database.DatabaseHelper.BeerTallies.targetuserid
import com.group2projc.Huishoud.database.DatabaseHelper.Users.displayname
import com.group2projc.Huishoud.database.DatabaseHelper.Users.id

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHelper(url: String) {
    //Singleton pattern for Database connection, Multiple connect calls will cause memory leaks.
    val db by lazy {
        Database.connect(url,
                driver = "org.postgresql.Driver",
                user = "postgres",
                password = "admin")

    }
    //Table definitions

    object Groups : Table() {
        val id = integer("groupid").primaryKey().autoIncrement()
        val created_at = varchar("created_at", 20)
        val name = varchar("name", 50)
    }

    //SQL : CREATE TABLE IF NOT EXISTS users (COLUMNS)
    object Users : Table() {
        val id = varchar("userid", 50).primaryKey()
        val groupid = reference("groupid", Groups.id).nullable()
        val global_permissions = varchar("global_permissions", 10)
        val displayname = varchar("displayname", 20)
        val picturelink = varchar("picturelink", 50)
    }

    object GroupPermissions : Table() {
        val groupid = reference("groupid", Groups.id).primaryKey()
        val userid = reference("userid", Users.id).primaryKey()
        val permission = varchar("permission", 10)
    }

    object Schedules : Table() {
        val groupid = reference("groupid", Groups.id).primaryKey()
        val useridto = reference("useridto", Users.id).primaryKey()
        val datedue = varchar("datetime", 20).primaryKey()
        val useridby = reference("useridby", Users.id)
        val description = varchar("description", 50)
    }

    object BeerTallies : Table() {
        val groupid = reference("groupid", Groups.id).primaryKey()
        val authorid = reference("authorid", Users.id).primaryKey()
        val date = varchar("date", 25).primaryKey()
        val targetuserid = reference("targetid", Users.id)
        val mutation = integer("mutation")
        val product = varchar("product", 50)

    }

    object InviteCodes : Table() {
        val id = integer("id").primaryKey().autoIncrement()
        val groupid = reference("groupid", Groups.id);
        val code = integer("code");
    }

    object Products: Table() {
        val groupid = reference("groupid",Groups.id).primaryKey()
        val name = varchar("name", 50).primaryKey()
        val price = float("price")
    }


//TODO: Find out if it's Possible to use DAO, find way to pass EntityID to postgres

//    //Entity (Row) objects for main tables.
//    // Support for Entities on tables with multiple primary keys is not supported yet :/
//    class Group(id:EntityID<Int>) : IntEntity(id){
//        companion object : IntEntityClass<Group>(Groups)
//        var createdAt by Groups.created_at
//        var name by Groups.name
//        var permissions by User via GroupPermissions
//
//    }
//
//    class User(id:EntityID<String>) : Entity<String>(id){
//        companion object : EntityClass<String, User>(Users)
//        var groupid by Group optionalReferencedOn Users.groupid
//        var globalPermissions by Users.global_permissions
//        var displayName by Users.displayname
//    }
//
//    class GroupPermission(id:EntityID<Int>) : IntEntity(id){
//        companion object : IntEntityClass<GroupPermission>(GroupPermissions)
//        var groupid by Group referencedOn GroupPermissions.groupid
//        var userid by User referencedOn GroupPermissions.userid
//        var permission by GroupPermissions.permission
//    }
//
//    class Schedule(id:EntityID<Int>) : IntEntity(id){
//        companion object : IntEntityClass<Schedule>(Schedules)
//        var groupid by Group referencedOn Schedules.groupid
//        var useridto by User referencedOn Schedules.useridto
//        var useridby by User referencedOn Schedules.useridby
//        var datedue by Schedules.datedue
//        var description by Schedules.description
//    }
//
//    class BeerTally(id:EntityID<Int>) : IntEntity(id){
//        companion object : IntEntityClass<BeerTally>(BeerTallies)
//        var groupid by Group referencedOn BeerTallies.groupid
//        var userid by User referencedOn BeerTallies.userid
//        var count by BeerTallies.count
//    }


    fun initDataBase(): DatabaseHelper {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Groups, Users, GroupPermissions, Schedules, BeerTallies, InviteCodes, Products)
        }

        return this@DatabaseHelper
    }

//    fun createGroup(n: String, uid: String): DatabaseHelper {
//        transaction(db) {
//            addLogger(StdOutSqlLogger)
//            val group = Groups.insert {
//                it[created_at] = LocalDate.now().toString()
//                it[name] = n
//            }
//            addUserToGroup(uid, group[Groups.id], creator = true)
//        }
//        return this@DatabaseHelper
//    }

    fun registerFireBaseUser(t: String, n: String): DatabaseHelper {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val query: Query = Users.select { Users.id eq t }
            if (query.count() == 0) {
                Users.insert {
                    it[id] = t
                    it[groupid] = null
                    it[global_permissions] = "user"
                    it[displayname] = n
                    it[picturelink] = ""
                }
            }

        }
        return this@DatabaseHelper
    }

    fun getUser(uid: String): HashMap<String, Any?> {
        var out = HashMap<String, Any?>()
        transaction(db) {
            Users.select({ Users.id eq uid }).forEach {
                out["uid"] = it[Users.id]
                out["groupid"] = it[Users.groupid]
                out["global_permissions"] = it[Users.global_permissions]
                out["display_name"] = it[Users.displayname]
                out["picture_link"] = it[Users.picturelink]
            }

            GroupPermissions.select ({ GroupPermissions.userid eq uid }).forEach {
                out["group_permissions"] = it[GroupPermissions.permission]
            }
        }
        return out
    }

    fun userUpdateDisplayName(uid: String, displayname1: String) : DatabaseHelper {
        transaction(db) {
            Users.update({ Users.id eq uid}){
                it[displayname] = displayname1
            }
        }
        return this@DatabaseHelper
    }

    fun userUpdatePicture(uid:String, picturePath: String) : DatabaseHelper {
        transaction(db) {
            Users.update({ Users.id eq uid}){
                it[picturelink] = picturePath
            }
        }
        return this@DatabaseHelper
    }


    fun getGroupName(gid: Int): HashMap<String, Any?> {
        var out = HashMap<String, Any?>()
        transaction(db) {
            Groups.select({ Groups.id eq gid }).forEach {
                out["groupid"] = it[Groups.id]
                out["created_at"] = it[Groups.created_at]
                out["name"] = it[Groups.name]
            }
        }
        return out
    }

    fun addUserToGroup(uid: String, gid: Int, makeUserAdmin: Boolean = false): DatabaseHelper {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            var group = 0
            Groups.select { Groups.id eq gid }.forEach {
                group = it[Groups.id]
            }
            Users.update({ Users.id eq uid }) {
                it[groupid] = group
            }

            var p = "user"
            if (makeUserAdmin)
                p = "groupAdmin"

            setGroupPermission(gid, uid, p)
        }
        return this@DatabaseHelper
    }

    fun setGroupPermission(gid: Int, uid: String, p: String): DatabaseHelper {
        transaction(db) {
            var gpg = 0
            var gpu = ""
            //Find if There is an entry for a permission for this user in this group

            GroupPermissions.select { (GroupPermissions.groupid eq gid) and (GroupPermissions.userid eq uid) }.forEach {
                gpg = it[GroupPermissions.groupid]
                gpu = it[GroupPermissions.userid]
            }
            //If yes, update it
            if (gpg != 0 && gpu != "") {
                GroupPermissions.update({ (GroupPermissions.groupid eq gpg) and (GroupPermissions.userid eq gpu) }) {
                    it[permission] = p
                }
                //If no, create it
            } else {
                GroupPermissions.insert {
                    it[groupid] = gid
                    it[userid] = uid
                    it[permission] = p
                }
            }
        }
        return this@DatabaseHelper
    }

    fun getTallyforGroup(gid: Int, product: String): HashMap<String, Int> {
        val uids = getAllInGroup(gid).values
        var out = HashMap<String, Int>()

        uids.forEach {
            out[it] = getBeerTally(gid,product,it)
        }

        return out;
    }

    fun getTallyForGroupByNameAndPic(gid: Int, product: String): HashMap<String, HashMap<String, Any>> {
        val uids = getAllInGroup(gid).values
        print(uids);
        var out = HashMap<String, HashMap<String, Any>>()

        uids.forEach {
            var user = getUser(it)
            var name: String? = user["display_name"] as String?
            var uid: String? = it

            if (name != null && uid != null) {
                var data = HashMap<String, Any>()
                data["picture"] = uid
                data["count"] = getBeerTally(gid,product,it)
                out[name] = data
            }
        }

        return out;
    }

    fun createBeerEntry(gid: Int, authoruid: String, targetuid: String, mutation: Int, product:String): DatabaseHelper {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val entry = DatabaseHelper.BeerTallies.insert {
                it[groupid] = gid
                it[authorid] = authoruid
                it[date] = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                        .toString()
                it[targetuserid] = targetuid
                it[BeerTallies.product] = product
                it[BeerTallies.mutation] = mutation
            }
        }
        return this@DatabaseHelper
    }

    fun getAllBeerEntriesForGroup(gid: Int): ArrayList<HashMap<String, Any>> {
        var outArr = ArrayList<HashMap<String,Any>>()
        transaction(db) {
            addLogger(StdOutSqlLogger)
            val authorUser = Users.alias("uAuthor")
            val targetUser = Users.alias("uTarget")
            (BeerTallies.innerJoin(authorUser,{authorid}, {authorUser[id]})
                    .innerJoin(targetUser,{targetuserid},{targetUser[id]}))
                    .select {(BeerTallies.groupid eq gid)}.forEach {
                        var out = HashMap<String, Any>()
                        out["gid"] = gid
                        out["authorid"] = it[authorid]
                        out["authorname"] = it[authorUser[displayname]]
                        out["targetid"] = it[targetuserid]
                        out["targetname"] = it[targetUser[displayname]]
                        out["product"] = it[product]
                        out["mutation"] = it[mutation]
                        out["date"] = it[date]
                        outArr.add(out)
                    }
        }
        return outArr
    }

    fun updateBeerEntry(gid: Int, author: String, target:String, date:String, mut: Int, prod:String) : DatabaseHelper{
        transaction(db) {
            BeerTallies.update ({(groupid eq gid) and (authorid eq author) and (targetuserid eq target) and (BeerTallies.date eq date) and (product eq prod)} ){
                it[mutation] = mut
            }
        }
        return this@DatabaseHelper
    }

    fun getBeerTally(gid: Int, product: String, targetuid: String): Int {
        var count = 0
        transaction(db) {
            addLogger(StdOutSqlLogger)
            BeerTallies
                    .slice(mutation)
                    .select {(targetuserid eq targetuid) and (BeerTallies.product eq product)}
                    .forEach {
                        val c = it[mutation]
                        if (c != null){
                            count += c
                        }
                    }
        }
        return count
    }

    fun getBeerTallyPerUserPerDay(gid: Int, targetuid: String): HashMap<Int, HashMap<String, Int>> {
        var out = HashMap<Int, HashMap<String, Int>>()
        var i = 0
        var count = 0
        transaction(db) {
            BeerTallies
                    .slice(mutation.sum(), date.substring(0,11))
                    .select {(targetuserid eq targetuid)}
                    .groupBy(date.substring(0,11))
                    .orderBy(date.substring(0,11))
                    .forEach {
                        var day = it[date.substring(0,11)]
                        val c = it[mutation.sum()]
                        if (c != null) {
                            count += c
                        }
                        var placeholder = HashMap<String, Int>()
                        placeholder[day] = count
                        i += 1
                        out[i] = placeholder
                    }
        }
        return out
    } // todo make it perday (groupby maybe?) todo: give days with 0 count still data...

    fun getAllInGroup(gid: Int): HashMap<String, String> {
        var out = HashMap<String, String>()
        transaction(db) {
            var i = 0
            Users.select { (Users.groupid eq gid) }.forEach {
                out["UserId${i}"] = it[Users.id]
                i++
            }
        }
        return out
    }

    fun createInviteCode(): Int {

        var random = Random();
        var key = random.nextInt(999999 - 100000) + 100000
        return key;



    }

    fun getInviteCode(gid : Int): HashMap<String, Int> {
        var keyFound = false;
        var finalKey  = 0;
        var out = HashMap<String, Int>();
        while (!keyFound) {
            var key = createInviteCode();
            var alreadyInUse = false;
            transaction(db) {
                InviteCodes.select { (InviteCodes.code eq key) }.forEach {
                    alreadyInUse = true;
                }
            }
            if(!alreadyInUse) {
                keyFound = true
                finalKey = key;
                out["code"] = finalKey;
            }
        }

        transaction(db) {
            InviteCodes.insert {
                it[groupid] = gid;
                it[code] = finalKey;
            }
        }
        return out;
    }

    fun joinGroubByCode(ic : Int, uid: String): HashMap<String, String>{
        var groupid : Int? = null;
        var out = HashMap<String, String>()
        transaction(db) {
            InviteCodes.select {(InviteCodes.code eq ic)}.forEach{
                groupid = it[InviteCodes.groupid];
            }
        }
        if(groupid != null) {
            transaction(db) {
                InviteCodes.deleteWhere {(InviteCodes.code eq ic)}
            }
            addUserToGroup(uid, groupid!!);
            out["result"] = "Succes";
        }
        else{
            out["result"] = "Code not found";
        }

        return out;
    }

    fun getAllProducts(gid:Int): HashMap<String,HashMap<String, Any>>{
        var out = HashMap<String, HashMap<String,Any>>()
        transaction(db) {
            var i = 0
            Products.select {(Products.groupid eq gid)}.forEach {
                out["${i}"] = HashMap<String, Any>()
                out["${i}"]?.set("name", it[Products.name])
                out["${i}"]?.set("price", it[Products.price])
                i++
            }
        }
        return out
    }

    fun addProduct(gid:Int,name:String,price:Float):DatabaseHelper{
        transaction(db) {
            Products.insert {
                it[Products.groupid] = gid
                it[Products.name] = name
                it[Products.price] = price
            }
        }

        return this@DatabaseHelper
    }

}

fun DatabaseHelper.createGroup(n: String, uid: String): DatabaseHelper {
    transaction(db) {
        addLogger(StdOutSqlLogger)
        val group = DatabaseHelper.Groups.insert {
            it[created_at] = LocalDate.now().toString()
            it[name] = n
        }
        addUserToGroup(uid, group[DatabaseHelper.Groups.id], makeUserAdmin = true)
    }
    return this
}