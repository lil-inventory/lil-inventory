package org.ivcode.inventory.repository

import org.apache.ibatis.annotations.*
import org.ivcode.inventory.repository.model.CheckoutEntity

private const val CREATE_CHECKOUT = """
    INSERT INTO checkout (asset_id, user_id, notes, timestamp)
    VALUES (#{assetId}, #{userId}, #{notes}, NOW())
"""

private const val READ_CHECKOUT = """
    SELECT
        checkout.*,
        `user`.display_name
    FROM
        checkout
        INNER JOIN `user` ON checkout.user_id=user.user_id
    WHERE
        asset_id=#{assetId}
"""



private const val DELETE_CHECKOUT =
    "DELETE FROM checkout WHERE checkout_id=#{checkoutId}"

@Mapper
interface CheckoutDao {

    @Insert(CREATE_CHECKOUT)
    fun createCheckout(assetId: Long, userId: Long, notes: String?): Int

    @Select(READ_CHECKOUT)
    @Result(property = "checkoutId", column = "checkout_id")
    @Result(property = "assetId", column = "asset_id")
    @Result(property = "userDisplayName", column = "display_name")
    @Result(property = "notes", column = "notes")
    @Result(property = "timestamp", column = "timestamp")
    fun readCheckouts(assetId: Long): List<CheckoutEntity>

    @Delete(DELETE_CHECKOUT)
    fun deleteCheckouts(checkoutId: Long): Int
}