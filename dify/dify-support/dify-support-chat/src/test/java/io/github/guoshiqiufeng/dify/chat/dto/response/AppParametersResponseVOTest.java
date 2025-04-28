/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.chat.dto.response;

import io.github.guoshiqiufeng.dify.chat.dto.response.parameter.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppParametersResponseVO}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class AppParametersResponseVOTest {

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String openingStatement = "Welcome to the assistant!";
        List<String> suggestedQuestions = Arrays.asList("How can I help?", "What services do you offer?");
        Enabled suggestedQuestionsAfterAnswer = new Enabled();
        suggestedQuestionsAfterAnswer.setEnabled(true);

        Enabled speechToText = new Enabled();
        speechToText.setEnabled(false);

        TextToSpeech textToSpeech = new TextToSpeech();
        textToSpeech.setEnabled(true);

        Enabled retrieverResource = new Enabled();
        retrieverResource.setEnabled(true);

        Enabled annotationReply = new Enabled();
        annotationReply.setEnabled(false);

        Enabled moreLikeThis = new Enabled();
        moreLikeThis.setEnabled(true);

        List<UserInputForm> userInputForm = new ArrayList<>();
        UserInputForm form = new UserInputForm();

        // Set properties for UserInputForm according to its actual structure
        TextInput textInput = new TextInput();
        textInput.setLabel("Enter your name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form.setTextInput(textInput);

        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Enter your description");
        paragraph.setVariable("description");
        paragraph.setRequired(false);
        form.setParagraph(paragraph);

        Select select = new Select();
        select.setLabel("Select your option");
        select.setVariable("option");
        select.setRequired(true);
        form.setSelect(select);

        userInputForm.add(form);

        Enabled sensitiveWordAvoidance = new Enabled();
        sensitiveWordAvoidance.setEnabled(true);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setEnabled(true);

        // Set properties for FileUploadConfig according to its actual structure
        FileUploadConfig systemParameters = new FileUploadConfig();
        systemParameters.setFileSizeLimit(10);
        systemParameters.setBatchCountLimit(5);
        systemParameters.setImageFileSizeLimit(2);
        systemParameters.setVideoFileSizeLimit(100);
        systemParameters.setAudioFileSizeLimit(50);
        systemParameters.setWorkflowFileUploadLimit(20);

        // Act
        AppParametersResponseVO responseVO = new AppParametersResponseVO();
        responseVO.setOpeningStatement(openingStatement);
        responseVO.setSuggestedQuestions(suggestedQuestions);
        responseVO.setSuggestedQuestionsAfterAnswer(suggestedQuestionsAfterAnswer);
        responseVO.setSpeechToText(speechToText);
        responseVO.setTextToSpeech(textToSpeech);
        responseVO.setRetrieverResource(retrieverResource);
        responseVO.setAnnotationReply(annotationReply);
        responseVO.setMoreLikeThis(moreLikeThis);
        responseVO.setUserInputForm(userInputForm);
        responseVO.setSensitiveWordAvoidance(sensitiveWordAvoidance);
        responseVO.setFileUpload(fileUpload);
        responseVO.setSystemParameters(systemParameters);

        // Assert
        assertEquals(openingStatement, responseVO.getOpeningStatement());
        assertEquals(suggestedQuestions, responseVO.getSuggestedQuestions());
        assertSame(suggestedQuestionsAfterAnswer, responseVO.getSuggestedQuestionsAfterAnswer());
        assertSame(speechToText, responseVO.getSpeechToText());
        assertSame(textToSpeech, responseVO.getTextToSpeech());
        assertSame(retrieverResource, responseVO.getRetrieverResource());
        assertSame(annotationReply, responseVO.getAnnotationReply());
        assertSame(moreLikeThis, responseVO.getMoreLikeThis());
        assertSame(userInputForm, responseVO.getUserInputForm());
        assertSame(sensitiveWordAvoidance, responseVO.getSensitiveWordAvoidance());
        assertSame(fileUpload, responseVO.getFileUpload());
        assertSame(systemParameters, responseVO.getSystemParameters());

        // Assert UserInputForm specific properties
        assertNotNull(responseVO.getUserInputForm().get(0).getTextInput());
        assertEquals("Enter your name", responseVO.getUserInputForm().get(0).getTextInput().getLabel());
        assertEquals("name", responseVO.getUserInputForm().get(0).getTextInput().getVariable());
        assertTrue(responseVO.getUserInputForm().get(0).getTextInput().getRequired());

        assertNotNull(responseVO.getUserInputForm().get(0).getParagraph());
        assertEquals("Enter your description", responseVO.getUserInputForm().get(0).getParagraph().getLabel());
        assertEquals("description", responseVO.getUserInputForm().get(0).getParagraph().getVariable());
        assertFalse(responseVO.getUserInputForm().get(0).getParagraph().getRequired());

        assertNotNull(responseVO.getUserInputForm().get(0).getSelect());
        assertEquals("Select your option", responseVO.getUserInputForm().get(0).getSelect().getLabel());
        assertEquals("option", responseVO.getUserInputForm().get(0).getSelect().getVariable());
        assertTrue(responseVO.getUserInputForm().get(0).getSelect().getRequired());

        // Assert FileUploadConfig specific properties
        assertEquals(Integer.valueOf(10), responseVO.getSystemParameters().getFileSizeLimit());
        assertEquals(Integer.valueOf(5), responseVO.getSystemParameters().getBatchCountLimit());
        assertEquals(Integer.valueOf(2), responseVO.getSystemParameters().getImageFileSizeLimit());
        assertEquals(Integer.valueOf(100), responseVO.getSystemParameters().getVideoFileSizeLimit());
        assertEquals(Integer.valueOf(50), responseVO.getSystemParameters().getAudioFileSizeLimit());
        assertEquals(Integer.valueOf(20), responseVO.getSystemParameters().getWorkflowFileUploadLimit());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        AppParametersResponseVO responseVO1 = new AppParametersResponseVO();
        responseVO1.setOpeningStatement("Welcome!");
        List<String> suggestedQuestions = Arrays.asList("Question 1", "Question 2");
        responseVO1.setSuggestedQuestions(suggestedQuestions);

        AppParametersResponseVO responseVO2 = new AppParametersResponseVO();
        responseVO2.setOpeningStatement("Welcome!");
        responseVO2.setSuggestedQuestions(suggestedQuestions);

        AppParametersResponseVO responseVO3 = new AppParametersResponseVO();
        responseVO3.setOpeningStatement("Hello!");
        responseVO3.setSuggestedQuestions(Arrays.asList("Different question"));

        // Assert
        assertEquals(responseVO1, responseVO2);
        assertEquals(responseVO1.hashCode(), responseVO2.hashCode());
        assertNotEquals(responseVO1, responseVO3);
        assertNotEquals(responseVO1.hashCode(), responseVO3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        AppParametersResponseVO responseVO = new AppParametersResponseVO();
        responseVO.setOpeningStatement("Welcome to the assistant!");
        responseVO.setSuggestedQuestions(Arrays.asList("How can I help?"));

        // Create and set up a UserInputForm with proper structure
        UserInputForm form = new UserInputForm();
        TextInput textInput = new TextInput();
        textInput.setLabel("Enter your name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form.setTextInput(textInput);
        responseVO.setUserInputForm(Arrays.asList(form));

        // Create and set up a FileUploadConfig with proper structure
        FileUploadConfig config = new FileUploadConfig();
        config.setFileSizeLimit(10);
        config.setBatchCountLimit(5);
        responseVO.setSystemParameters(config);

        // Act
        String toString = responseVO.toString();

        // Assert
        assertTrue(toString.contains("openingStatement=Welcome to the assistant!"));
        assertTrue(toString.contains("suggestedQuestions=[How can I help?]"));
        assertTrue(toString.contains("userInputForm="));
        assertTrue(toString.contains("systemParameters="));
        assertTrue(toString.contains("fileSizeLimit=10"));
        assertTrue(toString.contains("batchCountLimit=5"));
    }

    @Test
    public void testSerializable() {
        // Verify that the class implements Serializable
        AppParametersResponseVO responseVO = new AppParametersResponseVO();
        assertTrue(responseVO instanceof java.io.Serializable);

        // Also verify serializable for nested components
        UserInputForm userInputForm = new UserInputForm();
        assertTrue(userInputForm instanceof java.io.Serializable);

        FileUploadConfig fileUploadConfig = new FileUploadConfig();
        assertTrue(fileUploadConfig instanceof java.io.Serializable);
    }

    @Test
    public void testDefaultConstructor() {
        // Act
        AppParametersResponseVO responseVO = new AppParametersResponseVO();

        // Assert
        assertNull(responseVO.getOpeningStatement());
        assertNull(responseVO.getSuggestedQuestions());
        assertNull(responseVO.getSuggestedQuestionsAfterAnswer());
        assertNull(responseVO.getSpeechToText());
        assertNull(responseVO.getTextToSpeech());
        assertNull(responseVO.getRetrieverResource());
        assertNull(responseVO.getAnnotationReply());
        assertNull(responseVO.getMoreLikeThis());
        assertNull(responseVO.getUserInputForm());
        assertNull(responseVO.getSensitiveWordAvoidance());
        assertNull(responseVO.getFileUpload());
        assertNull(responseVO.getSystemParameters());
    }

    @Test
    public void testUserInputFormStructure() {
        // Arrange
        UserInputForm form = new UserInputForm();

        // Text Input
        TextInput textInput = new TextInput();
        textInput.setLabel("Name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form.setTextInput(textInput);

        // Paragraph
        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Comments");
        paragraph.setVariable("comments");
        paragraph.setRequired(false);
        form.setParagraph(paragraph);

        // Select
        Select select = new Select();
        select.setLabel("Options");
        select.setVariable("options");
        select.setRequired(true);
        form.setSelect(select);

        // Assert
        assertNotNull(form.getTextInput());
        assertEquals("Name", form.getTextInput().getLabel());
        assertEquals("name", form.getTextInput().getVariable());
        assertTrue(form.getTextInput().getRequired());

        assertNotNull(form.getParagraph());
        assertEquals("Comments", form.getParagraph().getLabel());
        assertEquals("comments", form.getParagraph().getVariable());
        assertFalse(form.getParagraph().getRequired());

        assertNotNull(form.getSelect());
        assertEquals("Options", form.getSelect().getLabel());
        assertEquals("options", form.getSelect().getVariable());
        assertTrue(form.getSelect().getRequired());
    }

    @Test
    public void testFileUploadConfigStructure() {
        // Arrange
        FileUploadConfig config = new FileUploadConfig();
        config.setFileSizeLimit(10);
        config.setBatchCountLimit(5);
        config.setImageFileSizeLimit(2);
        config.setVideoFileSizeLimit(100);
        config.setAudioFileSizeLimit(50);
        config.setWorkflowFileUploadLimit(20);

        // Assert
        assertEquals(Integer.valueOf(10), config.getFileSizeLimit());
        assertEquals(Integer.valueOf(5), config.getBatchCountLimit());
        assertEquals(Integer.valueOf(2), config.getImageFileSizeLimit());
        assertEquals(Integer.valueOf(100), config.getVideoFileSizeLimit());
        assertEquals(Integer.valueOf(50), config.getAudioFileSizeLimit());
        assertEquals(Integer.valueOf(20), config.getWorkflowFileUploadLimit());
    }
}
