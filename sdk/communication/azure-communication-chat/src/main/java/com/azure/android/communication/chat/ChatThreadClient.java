// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.chat;

import com.azure.android.communication.chat.models.AddChatParticipantsResult;
import com.azure.android.communication.chat.models.ChatMessage;
import com.azure.android.communication.chat.models.ChatMessageReadReceipt;
import com.azure.android.communication.chat.models.ChatParticipant;
import com.azure.android.communication.chat.models.ChatThreadProperties;
import com.azure.android.communication.chat.models.ListChatMessagesOptions;
import com.azure.android.communication.chat.models.ListParticipantsOptions;
import com.azure.android.communication.chat.models.ListReadReceiptOptions;
import com.azure.android.communication.chat.models.SendChatMessageOptions;
import com.azure.android.communication.chat.models.SendChatMessageResult;
import com.azure.android.communication.chat.models.UpdateChatMessageOptions;
import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.core.logging.ClientLogger;
import com.azure.android.core.rest.Response;
import com.azure.android.core.rest.annotation.ReturnType;
import com.azure.android.core.rest.annotation.ServiceClient;
import com.azure.android.core.rest.annotation.ServiceMethod;
import com.azure.android.core.rest.util.paging.PagedIterable;
import com.azure.android.core.rest.util.paging.PagedResponse;
import com.azure.android.core.util.Context;
import com.azure.android.core.util.Function;

import java.util.concurrent.ExecutionException;

import java9.util.concurrent.CompletableFuture;

/**
 * Sync Client that supports chat thread operations.
 */
@ServiceClient(builder = ChatThreadClientBuilder.class, isAsync = false)
public final class ChatThreadClient {
    private final ClientLogger logger = new ClientLogger(ChatThreadClient.class);

    private final ChatThreadAsyncClient client;
    private final String chatThreadId;

    /**
     * Creates a ChatClient that sends requests to the chat service at {@code serviceEndpoint}. Each
     * service call goes through the {@code pipeline}.
     *
     * @param client The {@link ChatAsyncClient} that the client routes its request through.
     */
    ChatThreadClient(ChatThreadAsyncClient client) {
        this.client = client;
        this.chatThreadId = client.getChatThreadId();
    }

    /**
     * Get the thread id.
     *
     * @return the thread id.
     */
    public String getChatThreadId() {
        return chatThreadId;
    }


    /**
     * Gets chat thread properties.
     *
     * @return the thread properties.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public ChatThreadProperties getProperties() {
        return block(this.client.getProperties());
    }

    /**
     * Gets chat thread properties.
     *
     * @param context The context to associate with this operation.
     *
     * @return the thread with the given id.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<ChatThreadProperties> getPropertiesWithResponse(Context context) {
        return block(this.client.getProperties(context));
    }

    /**
     * Updates a thread's topic.
     *
     * @param topic The new topic.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void updateTopic(String topic) {
        block(this.client.updateTopic(topic));
    }

    /**
     * Updates a thread's topic.
     *
     * @param topic The new topic.
     * @param context The context to associate with this operation.
     *
     * @return the response of the update request.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> updateTopicWithResponse(String topic, Context context) {
        return block(this.client.updateTopic(topic, context));
    }

    /**
     * Adds participants to a thread. If participants already exist, no change occurs.
     *
     * @param participants Participants to add.
     * @return the add participants result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public AddChatParticipantsResult addParticipants(Iterable<ChatParticipant> participants) {
        return block(this.client.addParticipants(participants));
    }

    /**
     * Adds participants to a thread. If participants already exist, no change occurs.
     *
     * @param participants Participants to add.
     * @param context the context to associate with this operation.
     *
     * @return the response containing operation result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<AddChatParticipantsResult> addParticipantsWithResponse(
        Iterable<ChatParticipant> participants, Context context) {
        return block(this.client.addParticipants(participants, context));
    }

    /**
     * Adds a participant to a thread. If the participant already exists, no change occurs.
     *
     * @param participant The new participant.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void addParticipant(ChatParticipant participant) {
        block(this.client.addParticipant(participant));
    }

    /**
     * Adds a participant to a thread. If the participant already exists, no change occurs.
     *
     * @param participant The new participant.
     * @param context The context to associate with this operation.
     *
     * @return the response containing operation result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> addParticipantWithResponse(ChatParticipant participant, Context context) {
        return block(this.client.addParticipant(participant, context));
    }

    /**
     * Remove a participant from a thread.
     *
     * @param identifier Identity of the participant to remove from the thread.
     * @param context The context to associate with this operation.
     *
     * @return the response of the remove request.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> removeParticipantWithResponse(CommunicationIdentifier identifier, Context context) {
        return block(this.client.removeParticipant(identifier, context));
    }

    /**
     * Remove a participant from a thread.
     *
     * @param identifier Identity of the thread participant to remove from the thread.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void removeParticipant(CommunicationIdentifier identifier) {
        block(this.client.removeParticipant(identifier));
    }


    /**
     * Gets the list of the thread participants.
     *
     * @return the list of the thread participants.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatParticipant> listParticipants() {
        return this.listParticipants(new ListParticipantsOptions(), Context.NONE);
    }

    /**
     * Gets the list of the thread participants.
     *
     * @param listParticipantsOptions the list options.
     * @param context the context to associate with this operation.
     *
     * @return the list of the thread participants.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatParticipant> listParticipants(
        ListParticipantsOptions listParticipantsOptions,
        Context context) {
        final Function<String, PagedResponse<ChatParticipant>> pageRetriever = (String pageId) -> {
            if (pageId == null) {
                return this.getParticipantsFirstPageWithResponse(listParticipantsOptions, context);
            } else {
                return this.getParticipantsNextPageWithResponse(pageId, context);
            }
        };
        return new PagedIterable<>(pageRetriever, pageId -> pageId != null, this.logger);
    }

    /**
     * Gets the list of the thread participants in the first page.
     *
     * @param listParticipantsOptions the list options.
     * @param context the context to associate with this operation.
     *
     * @return the list of the thread participants in the first page.
     */
    private PagedResponse<ChatParticipant> getParticipantsFirstPageWithResponse(
        ListParticipantsOptions listParticipantsOptions,
        Context context) {
        return block(this.client.getParticipantsFirstPage(listParticipantsOptions, context));
    }

    /**
     * Gets the page with given id containing list of thread participants.
     *
     * @param nextLink the identifier for the page to retrieve.
     * @param context the context to associate with this operation.
     *
     * @return the response containing the list of thread participants in the first page.
     */
    private PagedResponse<ChatParticipant> getParticipantsNextPageWithResponse(String nextLink,
                                                                              Context context) {
        return block(this.client.getParticipantsNextPage(nextLink, context));
    }

    /**
     * Sends a message to a thread.
     *
     * @param options options for sending the message.
     * @param context the context to associate with this operation.
     *
     * @return the response containing the send message result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<SendChatMessageResult> sendMessageWithResponse(SendChatMessageOptions options, Context context) {
        return block(this.client.sendMessage(options, context));
    }

    /**
     * Sends a message to a thread.
     *
     * @param options options for sending the message.
     * @return the send message result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public SendChatMessageResult sendMessage(SendChatMessageOptions options) {
        return block(this.client.sendMessage(options));
    }

    /**
     * Gets a message by id.
     *
     * @param chatMessageId the message id.
     * @param context the context to associate with this operation.
     *
     * @return the response containing the chat message.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<ChatMessage> getMessageWithResponse(String chatMessageId, Context context) {
        return block(this.client.getMessage(chatMessageId, context));
    }

    /**
     * Gets a message by id.
     *
     * @param chatMessageId The message id.
     *
     * @return a message by id.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public ChatMessage getMessage(String chatMessageId) {
        return block(this.client.getMessage(chatMessageId));
    }

    /**
     * Gets the list of thread messages.
     *
     * @return the list of thread messages.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatMessage> listMessages() {
        return this.listMessages(new ListChatMessagesOptions(), Context.NONE);
    }

    /**
     * Gets the list of thread messages.
     *
     * @param listMessagesOptions the list options.
     * @param context the context to associate with this operation.
     *
     * @return the list of thread messages.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatMessage> listMessages(ListChatMessagesOptions listMessagesOptions,
        Context context) {
        final Function<String, PagedResponse<ChatMessage>> pageRetriever = (String pageId) -> {
            if (pageId == null) {
                return this.getMessagesFirstPageWithResponse(listMessagesOptions, context);
            } else {
                return this.getMessagesNextPageWithResponse(pageId, context);
            }
        };
        return new PagedIterable<>(pageRetriever, pageId -> pageId != null, this.logger);
    }

    /**
     * Gets the list of thread messages in the first page.
     *
     * @param listMessagesOptions the list options.
     * @param context the context to associate with this operation.
     *
     * @return the response containing the list of thread messages.
     */
    private PagedResponse<ChatMessage> getMessagesFirstPageWithResponse(ListChatMessagesOptions listMessagesOptions,
        Context context) {
        return block(this.client.getMessagesFirstPage(listMessagesOptions, context));
    }

    /**
     * Gets the list of thread messages in the page with the given id.
     *
     * @param nextLink the identifier for the page to retrieve.
     * @param context The context to associate with this operation.
     *
     * @return the response containing the list of thread messages.
     */
    private PagedResponse<ChatMessage> getMessagesNextPageWithResponse(String nextLink, Context context) {
        return block(this.client.getMessagesNextPage(nextLink, context));
    }

    /**
     * Updates a message.
     *
     * @param chatMessageId the message id.
     * @param options options for updating the message.
     * @param context the context to associate with this operation.
     *
     * @return the response of the update request.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> updateMessageWithResponse(
        String chatMessageId, UpdateChatMessageOptions options, Context context) {
        return block(this.client.updateMessage(chatMessageId, options, context));
    }

    /**
     * Updates a message.
     *
     * @param chatMessageId the message id.
     * @param options options for updating the message.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void updateMessage(String chatMessageId, UpdateChatMessageOptions options) {
        block(this.client.updateMessage(chatMessageId, options));
    }

    /**
     * Deletes a message.
     *
     * @param chatMessageId the message id.
     * @param context the context to associate with this operation.
     *
     * @return the response of the delete request.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> deleteMessageWithResponse(String chatMessageId, Context context) {
        return block(this.client.deleteMessage(chatMessageId, context));
    }

    /**
     * Deletes a message.
     *
     * @param chatMessageId the message id.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void deleteMessage(String chatMessageId) {
        block(this.client.deleteMessage(chatMessageId));
    }

    /**
     * Posts a typing event to a thread, on behalf of a user.
     *
     * @param context the context to associate with this operation.
     *
     * @return the completion.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> sendTypingNotificationWithResponse(Context context) {
        return block(this.client.sendTypingNotification(context));
    }

    /**
     * Posts a typing event to a thread, on behalf of a user.
     *
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void sendTypingNotification() {
        block(this.client.sendTypingNotification());
    }

    /**
     * Posts a read receipt event to a thread, on behalf of a user.
     *
     * @param chatMessageId The id of the chat message that was read.
     * @param context The context to associate with this operation.
     *
     * @return the response containing the operation result.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Void> sendReadReceiptWithResponse(String chatMessageId, Context context) {
        return block(this.client.sendReadReceipt(chatMessageId, context));
    }

    /**
     * Posts a read receipt event to a thread, on behalf of a user.
     *
     * @param chatMessageId The id of the chat message that was read.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public void sendReadReceipt(String chatMessageId) {
        block(this.client.sendReadReceipt(chatMessageId));
    }

    /**
     * Gets the list of thread read receipts.
     *
     * @return the list of thread read receipts.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatMessageReadReceipt> listReadReceipts() {
        return this.listReadReceipts(new ListReadReceiptOptions(), Context.NONE);
    }

    /**
     * Gets the list of thread read receipts.
     *
     * @param listReadReceiptOptions the list options.
     * @param context the context to associate with this operation.
     *
     * @return the list of thread read receipts.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<ChatMessageReadReceipt> listReadReceipts(ListReadReceiptOptions listReadReceiptOptions,
                                                                 Context context) {
        final Function<String, PagedResponse<ChatMessageReadReceipt>> pageRetriever = (String pageId) -> {
            if (pageId == null) {
                return this.getReadReceiptsFirstPageWithResponse(listReadReceiptOptions, context);
            } else {
                return this.getReadReceiptsNextPageWithResponse(pageId, context);
            }
        };
        return new PagedIterable<>(pageRetriever, pageId -> pageId != null, this.logger);
    }

    /**
     * Gets the list of thread read receipts in the first page.
     *
     * @param listReadReceiptOptions the list options.
     * @param context The context to associate with this operation.
     *
     * @return the response containing the list of thread read receipts in the first page.
     */
    private PagedResponse<ChatMessageReadReceipt> getReadReceiptsFirstPageWithResponse(
        ListReadReceiptOptions listReadReceiptOptions,
        Context context) {
        return block(this.client.getReadReceiptsFirstPage(listReadReceiptOptions, context));
    }

    /**
     * Gets the list of thread read receipts in the page with the given id.
     *
     * @param nextLink the identifier for the page to retrieve.
     * @param context the context to associate with this operation.
     *
     * @return the response containing the list of thread read receipts in the first page.
     */
    private PagedResponse<ChatMessageReadReceipt> getReadReceiptsNextPageWithResponse(
        String nextLink,
        Context context) {
        return block(this.client.getReadReceiptsNextPage(nextLink, context));
    }

    private <T> T block(CompletableFuture<T> completableFuture) {
        try {
            return completableFuture.get();
        } catch (InterruptedException e) {
            throw logger.logExceptionAsError(new RuntimeException(e));
        } catch (ExecutionException e) {
            throw logger.logExceptionAsError(new RuntimeException(e));
        }
    }
}
