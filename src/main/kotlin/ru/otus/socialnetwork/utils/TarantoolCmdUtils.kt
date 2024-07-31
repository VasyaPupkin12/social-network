package ru.otus.socialnetwork.utils

/**
 * TarantoolCmdUtils.
 *
 * @author Vasily Kuchkin
 */

const val NAME_FIELD = "name"
const val KEY_FIELD = "key"
const val KEY_USER_FIELD = "user_id"
const val VALUE_FIELD = "value"
const val TYPE = "type"
const val STRING_TYPE = "string"
const val PARTS = "parts"
const val UNIQUE = "unique"
const val IF_NOT_EXISTS = "if_not_exists"

const val GET_SPACES_CMD = "return box.space"
const val CREATE_SPACE_CMD_TEMPLATE = "box.schema.space.create('%s')"
const val FORMAT_SPACE_CMD_TEMPLATE = "box.space.%s:format"
const val CREATE_INDEX_CMD_TEMPLATE = "box.space.%s:create_index"
const val SELECT_FEED_CMD_TEMPLATE = "return box.space.social_network_feed.index.USER_INDEX_social_network_feed:select(%s)"