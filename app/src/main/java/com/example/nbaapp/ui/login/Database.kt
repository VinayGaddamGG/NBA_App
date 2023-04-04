package com.example.nbaapp.ui.login
import android.util.Log
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import java.util.*
import kotlin.system.exitProcess

@Suppress("UnusedImport")
class Database {

    suspend fun addUser1(
        ddb: DynamoDbClient,
        tableNameVal: String,
        keyName: String,
        keyVal: String,
        username: String,
        usernameValue: String,
        password: String,
        passwordValue: String
    ) {


        val items = mutableMapOf<String, AttributeValue>()
        items[keyName] = AttributeValue.S(keyVal)
        items[username] = AttributeValue.S(usernameValue)
        items[password] = AttributeValue.S(passwordValue)

        val request = PutItemRequest {
            tableName = tableNameVal
            item = items
        }


        try {
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")


        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }


    }

    suspend fun updateUserName(
        tableNameVal: String,
        keyName: String,
        keyVal: String,
        username: String,
        usernameUpdated: String,

        ) {
        val items = mutableMapOf<String, AttributeValue>()
        items[keyName] = AttributeValue.S(keyVal)

        val updatedUsername = mutableMapOf<String, AttributeValueUpdate>()
        updatedUsername[username] = AttributeValueUpdate {
            value = AttributeValue.S(usernameUpdated)
            action = AttributeAction.Put
        }

        val request = UpdateItemRequest {
            tableName = tableNameVal
            key = items
            attributeUpdates = updatedUsername
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb -> ddb.updateItem(request) }

    }

    suspend fun updateUserPassword(
        tableNameVal: String,
        keyName: String,
        keyVal: String,
        password: String,
        passwordUpdated: String,

        ) {
        val items = mutableMapOf<String, AttributeValue>()
        items[keyName] = AttributeValue.S(keyVal)

        val updatedPassword = mutableMapOf<String, AttributeValueUpdate>()
        updatedPassword[password] = AttributeValueUpdate {
            value = AttributeValue.S(passwordUpdated)
            action = AttributeAction.Put
        }

        val request = UpdateItemRequest {
            tableName = tableNameVal
            key = items
            attributeUpdates = updatedPassword
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb -> ddb.updateItem(request) }

    }

    suspend fun deleteUser(
        tableNameVal: String,
        keyName: String,
        keyVal: String
    ) {
        val getKey = mutableMapOf<String, AttributeValue>()
        getKey[keyName] = AttributeValue.S(keyVal)

        val request = DeleteItemRequest {
            tableName = tableNameVal
            key = getKey
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb -> ddb.deleteItem(request) }
    }


    suspend fun scanForUsername(
        ddb: DynamoDbClient,
        tableNameVal: String,
        partitionKeyName: String,
        partitionKeyVal: String,
        partitionAlias: String,
    ): Int {

        val attrNameAlias = mutableMapOf<String, String>()
        attrNameAlias[partitionAlias] = partitionKeyName

        val attrValues = mutableMapOf<String, AttributeValue>()
        attrValues[":$partitionKeyName"] = AttributeValue.S(partitionKeyVal)

        val request = QueryRequest {
            tableName = tableNameVal
            keyConditionExpression = "$partitionAlias = :$partitionKeyName"
            expressionAttributeNames = attrNameAlias
            this.expressionAttributeValues = attrValues
        }


        var response = 0
        try {
            response = ddb.query(request).count
        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
            Log.d("TAG", "error")
        }

        return response

    }

    suspend fun getItem(
        ddb: DynamoDbClient,
        tableNameVal: String,
        partitionKeyName: String,
        partitionKeyVal: String,
        passwordUser : String
    ): Boolean {


        val keyToGet = mutableMapOf<String, AttributeValue>()
        keyToGet[partitionKeyName] = AttributeValue.S(partitionKeyVal)

        val request = GetItemRequest {
            key = keyToGet
            tableName = tableNameVal
            projectionExpression = "Password"
        }


        try {
            val returnedItem = ddb.getItem(request).item
            val usernameExists = returnedItem != null
            val passwordGood = passwordUser == returnedItem?.get("Password").toString().removePrefix("S(value=").removeSuffix(")")

            return usernameExists && passwordGood


        } catch (ex: DynamoDbException) {
            Log.d("TAG", ex.message.toString())
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }


    }

}














