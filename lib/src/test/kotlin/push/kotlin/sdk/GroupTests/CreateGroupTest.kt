package push.kotlin.sdk.GroupTests

import BASE_64_IMAGE
import getNewSinger
import org.junit.jupiter.api.Test
import push.kotlin.sdk.*
import push.kotlin.sdk.ChatFunctions.ApproveOptions
import push.kotlin.sdk.ChatFunctions.ChatApprover
import push.kotlin.sdk.ChatFunctions.ChatSender
import push.kotlin.sdk.ChatFunctions.SendOptions
import push.kotlin.sdk.Group.PushGroup
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CreateGroupTest {
  @Test
  fun  createPublicGroupV2Test(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()
    val (member3,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            creatorAddress = newAddress,
            isPublic = true,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            config = PushGroup.GroupConfig(),
            rules = mapOf(),
            members = mutableListOf(member1,member2),
            admins = mutableListOf(member3),
            groupType =  "default",
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()
    assertEquals(group.groupName, "$newAddress group")
  }

  @Test
  fun  createPrivateGroupTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
      name = "$newAddress group",
      description = "group made my the user $newAddress for testing",
      image = BASE_64_IMAGE,
      members = mutableListOf(member1,member2),
      creatorAddress = newAddress,
      isPublic = false,
      creatorPgpPrivateKey = pgpPK,
      env = ENV.staging,
      admins = mutableListOf(),
      config = PushGroup.GroupConfig(),
      rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()
    assertEquals(group.groupName, "$newAddress group")
  }

  @Test
  fun getGroupTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1,member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val gotGroup = PushGroup.getGroup(group.chatId, ENV.staging) ?: throw IllegalStateException("")

    assertEquals(gotGroup.groupName, "$newAddress group")
  }

  @Test
  fun getGroupMemberStatusTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptions(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1,member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging
    )

    val group = PushGroup.createGroup(createOptions).getOrThrow()

    val status = PushGroup.getGroupMemberStatus(group.chatId, newUser.did, ENV.staging)

    assertEquals(status!!.isMember, true)
  }

  @Test
  fun getGroupMembersPublicKeysTest() {
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1, _) = getNewSinger()
    val (member2, _) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1, member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()
    val option = PushGroup.FetchGroupMemberPublicKeysOptions(group.chatId, 1, 20,)
    val pKeys = PushGroup.getGroupMembersPublicKeys(option, ENV.staging)
    val item = pKeys!!.first()

    assertEquals(item.did, newUser.did)
  }

  @Test
  fun getGroupMembersTest() {
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1, _) = getNewSinger()
    val (member2, _) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1,member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )
    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val getOptions = PushGroup.FetchGroupMemberOptions(group.chatId)
    val members = PushGroup.getGroupMembers(getOptions, env = ENV.staging)


    assertEquals(members!!.size, 3)
  }

  @Test
  fun getAllGroupMembersTest() {
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1, _) = getNewSinger()
    val (member2, _) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1, member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )
    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val members = PushGroup.getAllGroupMembers(group.chatId, env = ENV.staging)
    val memberAddressList = mutableListOf<String>()

    for (member in members) {
      memberAddressList.add(member.address)
    }

    assertContains(memberAddressList, newUser.did)
  }

  @Test
  fun getAllGroupMembersPublicKeysTest() {
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1, _) = getNewSinger()
    val (member2, _) = getNewSinger()
    val (member3, _) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            creatorAddress = newAddress,
            isPublic = true,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            config = PushGroup.GroupConfig(),
            rules = mapOf(),
            members = mutableListOf(member1,member2),
            admins = mutableListOf(member3),
            groupType =  "default",
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val members = PushGroup.getAllGroupMembersPublicKeys(group.chatId, env = ENV.staging)
    val memberAddressList = mutableListOf<String>()

    for (member in members) {
      memberAddressList.add(member.did)
    }

    assertContains(memberAddressList, newUser.did)
  }

  @Test
  fun getGroupInfoTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1,member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            rules = mapOf(),
            config = PushGroup.GroupConfig()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val gotGroup = PushGroup.getGroupInfo(group.chatId, ENV.staging) ?: throw IllegalStateException("")

    assertEquals(gotGroup.groupName, "$newAddress group")
  }

  @Test
  fun getGroupMemberCountTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val (member2,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1,member2),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    val membersCount = PushGroup.getGroupMemberCount(group.chatId, ENV.staging) ?: throw IllegalStateException("")
    val total = membersCount.totalMembersCount.overallCount
    assertEquals(total, 3)
  }

  @Test
  fun getEmptyGroupTest(){
    val gotGroup = PushGroup.getGroup("WRONG_GROUP_ID", ENV.staging)
    assert(gotGroup == null)
  }

  @Test
  fun joinGroupTest(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()

    val createOptions = PushGroup.CreateGroupOptionsV2(
            name = "$newAddress group",
            description = "group made my the user $newAddress for testing",
            image = BASE_64_IMAGE,
            members = mutableListOf(member1, PGP_LINKED_ADDRESS),
            creatorAddress = newAddress,
            isPublic = false,
            creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    ChatApprover(ApproveOptions(
            requesterAddress = group.chatId,
            approverAddress = PGP_LINKED_ADDRESS,
            pgpPrivateKey = PGP_PK,
            env = ENV.staging
    )).approve().getOrThrow()
  }

  @Test
  fun sendMessagesToPublicGroup(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    val (member1,_) = getNewSinger()
    val createOptions = PushGroup.CreateGroupOptionsV2(
        name = "$newAddress group",
        description = "group made my the user $newAddress for testing",
        image = BASE_64_IMAGE,
        members = mutableListOf(member1, PGP_LINKED_ADDRESS),
        creatorAddress = newAddress,
        isPublic = true,
        creatorPgpPrivateKey = pgpPK,
        env = ENV.staging,
        admins = mutableListOf(),
        config = PushGroup.GroupConfig(),
        rules = mapOf()
    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()
    ChatApprover(ApproveOptions(
      requesterAddress = group.chatId,
      approverAddress = PGP_LINKED_ADDRESS,
      pgpPrivateKey = PGP_PK,
      env = ENV.staging
    )).approve().getOrThrow()

    // send messages to the group
    val message1 = "Welcome to the group ${group.chatId} msg1"
    val message2 = "Welcome to the group ${group.chatId} msg2"

    ChatSender(SendOptions(
      messageContent = message1,
      messageType = "Text",
      receiverAddress = group.chatId,
      senderAddress = newAddress,
      senderPgpPrivateKey = pgpPK,
      env = ENV.staging,
    )).sendChat().getOrThrow()

    ChatSender(SendOptions(
      messageContent = message2,
      messageType = "Text",
      receiverAddress = group.chatId,
      senderAddress = PGP_LINKED_ADDRESS,
      senderPgpPrivateKey = PGP_PK,
      env = ENV.staging,
    )).sendChat().getOrThrow()
  }

  @Test
  fun sendMessagesToPrivateGroup(){
    val (newAddress, signer) = getNewSinger()
    val newUser = PushUser.createUser(signer, ENV.staging).getOrThrow()
    val pgpPK = DecryptPgp.decryptPgpKey(newUser.encryptedPrivateKey, signer).getOrThrow()

    // make the private group of 4
    val (member1,_) = getNewSinger()
    val (member2, signer2) = getNewSinger()
    val newUser2 = PushUser.createUser(signer2, ENV.staging).getOrThrow()
    val pgpPK2 = DecryptPgp.decryptPgpKey(newUser2.encryptedPrivateKey, signer2).getOrThrow()
    val createOptions = PushGroup.CreateGroupOptionsV2(
      name = "$newAddress group",
      description = "group made my the user $newAddress for testing",
      image = BASE_64_IMAGE,
      members = mutableListOf(member1, PGP_LINKED_ADDRESS),
      creatorAddress = newAddress,
      isPublic = false,
      creatorPgpPrivateKey = pgpPK,
            env = ENV.staging,
            admins = mutableListOf(),
            config = PushGroup.GroupConfig(),
            rules = mapOf()

    )

    val group = PushGroup.createGroupV2(createOptions).getOrThrow()

    // Two user joins the group
    ChatApprover(ApproveOptions(
      requesterAddress = group.chatId,
      approverAddress = PGP_LINKED_ADDRESS,
      pgpPrivateKey = PGP_PK,
      env = ENV.staging
    )).approve().getOrThrow()
    ChatApprover(ApproveOptions(
      requesterAddress = group.chatId,
      approverAddress = member2,
      pgpPrivateKey = pgpPK2,
      env = ENV.staging
    )).approve().getOrThrow()

    // send messages to the group
    val message1 = "Wlecome to the group ${group.chatId} msg1"
    val message2 = "Wlecome to the group ${group.chatId} msg2"

    ChatSender(SendOptions(
      messageContent = message1,
      messageType = "Text",
      receiverAddress = group.chatId,
      senderAddress = member2,
      senderPgpPrivateKey = pgpPK2,
      env = ENV.staging,
    )).sendChat().getOrThrow()

    ChatSender(SendOptions(
      messageContent = message2,
      messageType = "Text",
      receiverAddress = group.chatId,
      senderAddress = PGP_LINKED_ADDRESS,
      senderPgpPrivateKey = PGP_PK,
      env = ENV.staging,
    )).sendChat().getOrThrow()
  }

}