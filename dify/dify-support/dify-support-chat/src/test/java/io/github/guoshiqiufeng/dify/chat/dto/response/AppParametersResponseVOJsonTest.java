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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.chat.dto.response.parameter.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppParametersResponseVO} JSON serialization/deserialization
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class AppParametersResponseVOJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{" +
                "\"opening_statement\":\"Welcome to the assistant!\"," +
                "\"suggested_questions\":[\"How can I help?\", \"What services do you offer?\"]," +
                "\"suggested_questions_after_answer\":{\"enabled\":true}," +
                "\"speech_to_text\":{\"enabled\":false}," +
                "\"text_to_speech\":{\"enabled\":true}," +
                "\"retriever_resource\":{\"enabled\":true}," +
                "\"annotation_reply\":{\"enabled\":false}," +
                "\"more_like_this\":{\"enabled\":true}," +
                "\"user_input_form\":[{" +
                "  \"text-input\":{" +
                "    \"label\":\"Enter your name\"," +
                "    \"variable\":\"name\"," +
                "    \"required\":true" +
                "  }," +
                "  \"paragraph\":{" +
                "    \"label\":\"Enter your description\"," +
                "    \"variable\":\"description\"," +
                "    \"required\":false" +
                "  }," +
                "  \"select\":{" +
                "    \"label\":\"Select your option\"," +
                "    \"variable\":\"option\"," +
                "    \"required\":true" +
                "  }" +
                "}]," +
                "\"sensitive_word_avoidance\":{\"enabled\":true}," +
                "\"file_upload\":{\"enabled\":true}," +
                "\"system_parameters\":{" +
                "  \"file_size_limit\":10," +
                "  \"batch_count_limit\":5," +
                "  \"image_file_size_limit\":2," +
                "  \"video_file_size_limit\":100," +
                "  \"audio_file_size_limit\":50," +
                "  \"workflow_file_upload_limit\":20" +
                "}" +
                "}";

        // Act
        AppParametersResponseVO responseVO = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertEquals("Welcome to the assistant!", responseVO.getOpeningStatement());
        assertEquals(Arrays.asList("How can I help?", "What services do you offer?"), responseVO.getSuggestedQuestions());

        // Check nested objects
        assertTrue(responseVO.getSuggestedQuestionsAfterAnswer().getEnabled());
        assertFalse(responseVO.getSpeechToText().getEnabled());
        assertTrue(responseVO.getTextToSpeech().getEnabled());
        assertTrue(responseVO.getRetrieverResource().getEnabled());
        assertFalse(responseVO.getAnnotationReply().getEnabled());
        assertTrue(responseVO.getMoreLikeThis().getEnabled());

        // Check user input form
        assertNotNull(responseVO.getUserInputForm());
        assertEquals(1, responseVO.getUserInputForm().size());

        // Check text-input control
        assertNotNull(responseVO.getUserInputForm().get(0).getTextInput());
        assertEquals("Enter your name", responseVO.getUserInputForm().get(0).getTextInput().getLabel());
        assertEquals("name", responseVO.getUserInputForm().get(0).getTextInput().getVariable());
        assertTrue(responseVO.getUserInputForm().get(0).getTextInput().getRequired());

        // Check paragraph control
        assertNotNull(responseVO.getUserInputForm().get(0).getParagraph());
        assertEquals("Enter your description", responseVO.getUserInputForm().get(0).getParagraph().getLabel());
        assertEquals("description", responseVO.getUserInputForm().get(0).getParagraph().getVariable());
        assertFalse(responseVO.getUserInputForm().get(0).getParagraph().getRequired());

        // Check select control
        assertNotNull(responseVO.getUserInputForm().get(0).getSelect());
        assertEquals("Select your option", responseVO.getUserInputForm().get(0).getSelect().getLabel());
        assertEquals("option", responseVO.getUserInputForm().get(0).getSelect().getVariable());
        assertTrue(responseVO.getUserInputForm().get(0).getSelect().getRequired());

        // Check remaining fields
        assertTrue(responseVO.getSensitiveWordAvoidance().getEnabled());
        assertTrue(responseVO.getFileUpload().getEnabled());

        // Check FileUploadConfig
        assertNotNull(responseVO.getSystemParameters());
        assertEquals(Integer.valueOf(10), responseVO.getSystemParameters().getFileSizeLimit());
        assertEquals(Integer.valueOf(5), responseVO.getSystemParameters().getBatchCountLimit());
        assertEquals(Integer.valueOf(2), responseVO.getSystemParameters().getImageFileSizeLimit());
        assertEquals(Integer.valueOf(100), responseVO.getSystemParameters().getVideoFileSizeLimit());
        assertEquals(Integer.valueOf(50), responseVO.getSystemParameters().getAudioFileSizeLimit());
        assertEquals(Integer.valueOf(20), responseVO.getSystemParameters().getWorkflowFileUploadLimit());
    }

    @Test
    public void testJsonDeserializationWithPartialData() throws Exception {
        // Arrange - JSON with only some fields
        String json = "{" +
                "\"opening_statement\":\"Welcome!\"," +
                "\"suggested_questions\":[\"Question 1\"]" +
                "}";

        // Act
        AppParametersResponseVO responseVO = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertEquals("Welcome!", responseVO.getOpeningStatement());
        assertEquals(List.of("Question 1"), responseVO.getSuggestedQuestions());

        // All other fields should be null
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
    public void testJsonDeserializationWithEmptyObjects() throws Exception {
        // Arrange - JSON with empty objects
        String json = "{" +
                "\"opening_statement\":\"Welcome!\"," +
                "\"suggested_questions_after_answer\":{}," +
                "\"speech_to_text\":{}," +
                "\"text_to_speech\":{}," +
                "\"user_input_form\":[]," +
                "\"system_parameters\":{}" +
                "}";

        // Act
        AppParametersResponseVO responseVO = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertEquals("Welcome!", responseVO.getOpeningStatement());

        // Empty objects should be initialized but with default values
        assertNotNull(responseVO.getSuggestedQuestionsAfterAnswer());
        assertNull(responseVO.getSuggestedQuestionsAfterAnswer().getEnabled());

        assertNotNull(responseVO.getSpeechToText());
        assertNull(responseVO.getSpeechToText().getEnabled());

        assertNotNull(responseVO.getTextToSpeech());
        assertNull(responseVO.getTextToSpeech().getEnabled());

        assertNotNull(responseVO.getUserInputForm());
        assertTrue(responseVO.getUserInputForm().isEmpty());

        // Empty FileUploadConfig should be initialized with null values
        assertNotNull(responseVO.getSystemParameters());
        assertNull(responseVO.getSystemParameters().getFileSizeLimit());
        assertNull(responseVO.getSystemParameters().getBatchCountLimit());
        assertNull(responseVO.getSystemParameters().getImageFileSizeLimit());
        assertNull(responseVO.getSystemParameters().getVideoFileSizeLimit());
        assertNull(responseVO.getSystemParameters().getAudioFileSizeLimit());
        assertNull(responseVO.getSystemParameters().getWorkflowFileUploadLimit());
    }

    @Test
    public void testJsonSerializationShouldUseStandardFieldNames() throws Exception {
        // Arrange
        AppParametersResponseVO responseVO = new AppParametersResponseVO();
        responseVO.setOpeningStatement("Welcome!");
        responseVO.setSuggestedQuestions(Arrays.asList("Question 1", "Question 2"));

        Enabled enabled = new Enabled();
        enabled.setEnabled(true);
        responseVO.setSuggestedQuestionsAfterAnswer(enabled);

        // Add a UserInputForm with TextInput
        UserInputForm form = new UserInputForm();
        TextInput textInput = new TextInput();
        textInput.setLabel("Your name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form.setTextInput(textInput);
        responseVO.setUserInputForm(List.of(form));

        // Add FileUploadConfig
        FileUploadConfig config = new FileUploadConfig();
        config.setFileSizeLimit(10);
        config.setBatchCountLimit(5);
        responseVO.setSystemParameters(config);

        // Act
        String json = objectMapper.writeValueAsString(responseVO);

        // Assert - When serializing, we should use standard field names (camelCase)
        assertTrue(json.contains("\"openingStatement\":\"Welcome!\""));
        assertTrue(json.contains("\"suggestedQuestions\":[\"Question 1\",\"Question 2\"]"));
        assertTrue(json.contains("\"suggestedQuestionsAfterAnswer\":{\"enabled\":true}"));

        // Check UserInputForm serialization
        assertTrue(json.contains("\"textInput\":{"));
        assertTrue(json.contains("\"label\":\"Your name\""));
        assertTrue(json.contains("\"variable\":\"name\""));
        assertTrue(json.contains("\"required\":true"));

        // Check FileUploadConfig serialization
        assertTrue(json.contains("\"systemParameters\":{"));
        assertTrue(json.contains("\"fileSizeLimit\":10"));
        assertTrue(json.contains("\"batchCountLimit\":5"));
    }

    @Test
    public void testJsonDeserializationWithUserInputFormControls() throws Exception {
        // Arrange - JSON with all UserInputForm controls
        String json = "{" +
                "\"user_input_form\":[" +
                "  {" +
                "    \"text-input\":{" +
                "      \"label\":\"Name\"," +
                "      \"variable\":\"name\"," +
                "      \"required\":true" +
                "    }" +
                "  }," +
                "  {" +
                "    \"paragraph\":{" +
                "      \"label\":\"Description\"," +
                "      \"variable\":\"description\"," +
                "      \"required\":false" +
                "    }" +
                "  }," +
                "  {" +
                "    \"select\":{" +
                "      \"label\":\"Options\"," +
                "      \"variable\":\"option\"," +
                "      \"required\":true," +
                "      \"options\":[\"Option 1\", \"Option 2\"]" +
                "    }" +
                "  }" +
                "]" +
                "}";

        // Act
        AppParametersResponseVO responseVO = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertNotNull(responseVO.getUserInputForm());
        assertEquals(3, responseVO.getUserInputForm().size());

        // First item has text-input
        assertNotNull(responseVO.getUserInputForm().get(0).getTextInput());
        assertEquals("Name", responseVO.getUserInputForm().get(0).getTextInput().getLabel());
        assertEquals("name", responseVO.getUserInputForm().get(0).getTextInput().getVariable());
        assertTrue(responseVO.getUserInputForm().get(0).getTextInput().getRequired());

        // Second item has paragraph
        assertNotNull(responseVO.getUserInputForm().get(1).getParagraph());
        assertEquals("Description", responseVO.getUserInputForm().get(1).getParagraph().getLabel());
        assertEquals("description", responseVO.getUserInputForm().get(1).getParagraph().getVariable());
        assertFalse(responseVO.getUserInputForm().get(1).getParagraph().getRequired());

        // Third item has select
        assertNotNull(responseVO.getUserInputForm().get(2).getSelect());
        assertEquals("Options", responseVO.getUserInputForm().get(2).getSelect().getLabel());
        assertEquals("option", responseVO.getUserInputForm().get(2).getSelect().getVariable());
        assertTrue(responseVO.getUserInputForm().get(2).getSelect().getRequired());
    }

    @Test
    public void testJsonDeserializationWithFileUploadConfig() throws Exception {
        // Arrange - JSON with FileUploadConfig
        String json = "{" +
                "\"system_parameters\":{" +
                "  \"file_size_limit\":10," +
                "  \"batch_count_limit\":5," +
                "  \"image_file_size_limit\":2," +
                "  \"video_file_size_limit\":100," +
                "  \"audio_file_size_limit\":50," +
                "  \"workflow_file_upload_limit\":20" +
                "}" +
                "}";

        // Act
        AppParametersResponseVO responseVO = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertNotNull(responseVO.getSystemParameters());
        assertEquals(Integer.valueOf(10), responseVO.getSystemParameters().getFileSizeLimit());
        assertEquals(Integer.valueOf(5), responseVO.getSystemParameters().getBatchCountLimit());
        assertEquals(Integer.valueOf(2), responseVO.getSystemParameters().getImageFileSizeLimit());
        assertEquals(Integer.valueOf(100), responseVO.getSystemParameters().getVideoFileSizeLimit());
        assertEquals(Integer.valueOf(50), responseVO.getSystemParameters().getAudioFileSizeLimit());
        assertEquals(Integer.valueOf(20), responseVO.getSystemParameters().getWorkflowFileUploadLimit());
    }

    @Test
    public void testJsonSerializationWithComplexStructure() throws Exception {
        // Arrange - Create a complex AppParametersResponseVO
        AppParametersResponseVO responseVO = new AppParametersResponseVO();
        responseVO.setOpeningStatement("Welcome to the assistant!");
        responseVO.setSuggestedQuestions(Arrays.asList("How can I help?", "What services do you offer?"));

        Enabled suggestedQuestionsAfterAnswer = new Enabled();
        suggestedQuestionsAfterAnswer.setEnabled(true);
        responseVO.setSuggestedQuestionsAfterAnswer(suggestedQuestionsAfterAnswer);

        TextToSpeech textToSpeech = new TextToSpeech();
        textToSpeech.setEnabled(true);
        responseVO.setTextToSpeech(textToSpeech);

        // Create UserInputForm with TextInput and Paragraph
        List<UserInputForm> userInputForm = new ArrayList<>();

        UserInputForm form1 = new UserInputForm();
        TextInput textInput = new TextInput();
        textInput.setLabel("Your name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form1.setTextInput(textInput);
        userInputForm.add(form1);

        UserInputForm form2 = new UserInputForm();
        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Your email");
        paragraph.setVariable("email");
        paragraph.setRequired(false);
        form2.setParagraph(paragraph);
        userInputForm.add(form2);

        responseVO.setUserInputForm(userInputForm);

        // Create FileUpload and FileUploadConfig
        FileUpload fileUpload = new FileUpload();
        fileUpload.setEnabled(true);
        responseVO.setFileUpload(fileUpload);

        FileUploadConfig systemParameters = new FileUploadConfig();
        systemParameters.setFileSizeLimit(10);
        systemParameters.setBatchCountLimit(5);
        responseVO.setSystemParameters(systemParameters);

        // Act
        String json = objectMapper.writeValueAsString(responseVO);
        AppParametersResponseVO deserializedResponse = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert
        assertEquals(responseVO.getOpeningStatement(), deserializedResponse.getOpeningStatement());
        assertEquals(responseVO.getSuggestedQuestions(), deserializedResponse.getSuggestedQuestions());
        assertEquals(responseVO.getSuggestedQuestionsAfterAnswer().getEnabled(),
                deserializedResponse.getSuggestedQuestionsAfterAnswer().getEnabled());
        assertEquals(responseVO.getTextToSpeech().getEnabled(), deserializedResponse.getTextToSpeech().getEnabled());

        // Assert UserInputForm
        assertEquals(2, deserializedResponse.getUserInputForm().size());

        assertNotNull(deserializedResponse.getUserInputForm().get(0).getTextInput());
        assertEquals("Your name", deserializedResponse.getUserInputForm().get(0).getTextInput().getLabel());
        assertEquals("name", deserializedResponse.getUserInputForm().get(0).getTextInput().getVariable());
        assertTrue(deserializedResponse.getUserInputForm().get(0).getTextInput().getRequired());

        assertNotNull(deserializedResponse.getUserInputForm().get(1).getParagraph());
        assertEquals("Your email", deserializedResponse.getUserInputForm().get(1).getParagraph().getLabel());
        assertEquals("email", deserializedResponse.getUserInputForm().get(1).getParagraph().getVariable());
        assertFalse(deserializedResponse.getUserInputForm().get(1).getParagraph().getRequired());

        // Assert FileUpload and FileUploadConfig
        assertEquals(responseVO.getFileUpload().getEnabled(), deserializedResponse.getFileUpload().getEnabled());
        assertEquals(responseVO.getSystemParameters().getFileSizeLimit(), deserializedResponse.getSystemParameters().getFileSizeLimit());
        assertEquals(responseVO.getSystemParameters().getBatchCountLimit(), deserializedResponse.getSystemParameters().getBatchCountLimit());
    }

    @Test
    public void testFullCycleSerialization() throws Exception {
        // Arrange - Create a full response object
        AppParametersResponseVO originalResponse = new AppParametersResponseVO();
        originalResponse.setOpeningStatement("Welcome to the assistant!");
        originalResponse.setSuggestedQuestions(Arrays.asList("How can I help?", "What services do you offer?"));

        Enabled suggestedQuestionsAfterAnswer = new Enabled();
        suggestedQuestionsAfterAnswer.setEnabled(true);
        originalResponse.setSuggestedQuestionsAfterAnswer(suggestedQuestionsAfterAnswer);

        Enabled speechToText = new Enabled();
        speechToText.setEnabled(false);
        originalResponse.setSpeechToText(speechToText);

        TextToSpeech textToSpeech = new TextToSpeech();
        textToSpeech.setEnabled(true);
        originalResponse.setTextToSpeech(textToSpeech);

        Enabled retrieverResource = new Enabled();
        retrieverResource.setEnabled(true);
        originalResponse.setRetrieverResource(retrieverResource);

        Enabled annotationReply = new Enabled();
        annotationReply.setEnabled(false);
        originalResponse.setAnnotationReply(annotationReply);

        Enabled moreLikeThis = new Enabled();
        moreLikeThis.setEnabled(true);
        originalResponse.setMoreLikeThis(moreLikeThis);

        // Setup UserInputForm with all control types
        List<UserInputForm> userInputForm = new ArrayList<>();

        UserInputForm form1 = new UserInputForm();
        TextInput textInput = new TextInput();
        textInput.setLabel("Your name");
        textInput.setVariable("name");
        textInput.setRequired(true);
        form1.setTextInput(textInput);
        userInputForm.add(form1);

        UserInputForm form2 = new UserInputForm();
        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Your description");
        paragraph.setVariable("description");
        paragraph.setRequired(false);
        form2.setParagraph(paragraph);
        userInputForm.add(form2);

        UserInputForm form3 = new UserInputForm();
        Select select = new Select();
        select.setLabel("Your option");
        select.setVariable("option");
        select.setRequired(true);
        form3.setSelect(select);
        userInputForm.add(form3);

        originalResponse.setUserInputForm(userInputForm);

        Enabled sensitiveWordAvoidance = new Enabled();
        sensitiveWordAvoidance.setEnabled(true);
        originalResponse.setSensitiveWordAvoidance(sensitiveWordAvoidance);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setEnabled(true);
        originalResponse.setFileUpload(fileUpload);

        // Setup complete FileUploadConfig
        FileUploadConfig systemParameters = new FileUploadConfig();
        systemParameters.setFileSizeLimit(10);
        systemParameters.setBatchCountLimit(5);
        systemParameters.setImageFileSizeLimit(2);
        systemParameters.setVideoFileSizeLimit(100);
        systemParameters.setAudioFileSizeLimit(50);
        systemParameters.setWorkflowFileUploadLimit(20);
        originalResponse.setSystemParameters(systemParameters);

        // Act - Serialize then deserialize
        String json = objectMapper.writeValueAsString(originalResponse);
        AppParametersResponseVO deserializedResponse = objectMapper.readValue(json, AppParametersResponseVO.class);

        // Assert - Compare original and deserialized objects
        assertEquals(originalResponse.getOpeningStatement(), deserializedResponse.getOpeningStatement());
        assertEquals(originalResponse.getSuggestedQuestions(), deserializedResponse.getSuggestedQuestions());

        assertEquals(originalResponse.getSuggestedQuestionsAfterAnswer().getEnabled(),
                deserializedResponse.getSuggestedQuestionsAfterAnswer().getEnabled());

        assertEquals(originalResponse.getSpeechToText().getEnabled(),
                deserializedResponse.getSpeechToText().getEnabled());

        assertEquals(originalResponse.getTextToSpeech().getEnabled(),
                deserializedResponse.getTextToSpeech().getEnabled());

        assertEquals(originalResponse.getRetrieverResource().getEnabled(),
                deserializedResponse.getRetrieverResource().getEnabled());

        assertEquals(originalResponse.getAnnotationReply().getEnabled(),
                deserializedResponse.getAnnotationReply().getEnabled());

        assertEquals(originalResponse.getMoreLikeThis().getEnabled(),
                deserializedResponse.getMoreLikeThis().getEnabled());

        // Assert UserInputForm controls
        assertEquals(3, deserializedResponse.getUserInputForm().size());

        // TextInput
        assertNotNull(deserializedResponse.getUserInputForm().get(0).getTextInput());
        assertEquals(originalResponse.getUserInputForm().get(0).getTextInput().getLabel(),
                deserializedResponse.getUserInputForm().get(0).getTextInput().getLabel());
        assertEquals(originalResponse.getUserInputForm().get(0).getTextInput().getVariable(),
                deserializedResponse.getUserInputForm().get(0).getTextInput().getVariable());
        assertEquals(originalResponse.getUserInputForm().get(0).getTextInput().getRequired(),
                deserializedResponse.getUserInputForm().get(0).getTextInput().getRequired());

        // Paragraph
        assertNotNull(deserializedResponse.getUserInputForm().get(1).getParagraph());
        assertEquals(originalResponse.getUserInputForm().get(1).getParagraph().getLabel(),
                deserializedResponse.getUserInputForm().get(1).getParagraph().getLabel());
        assertEquals(originalResponse.getUserInputForm().get(1).getParagraph().getVariable(),
                deserializedResponse.getUserInputForm().get(1).getParagraph().getVariable());
        assertEquals(originalResponse.getUserInputForm().get(1).getParagraph().getRequired(),
                deserializedResponse.getUserInputForm().get(1).getParagraph().getRequired());

        // Select
        assertNotNull(deserializedResponse.getUserInputForm().get(2).getSelect());
        assertEquals(originalResponse.getUserInputForm().get(2).getSelect().getLabel(),
                deserializedResponse.getUserInputForm().get(2).getSelect().getLabel());
        assertEquals(originalResponse.getUserInputForm().get(2).getSelect().getVariable(),
                deserializedResponse.getUserInputForm().get(2).getSelect().getVariable());
        assertEquals(originalResponse.getUserInputForm().get(2).getSelect().getRequired(),
                deserializedResponse.getUserInputForm().get(2).getSelect().getRequired());

        assertEquals(originalResponse.getSensitiveWordAvoidance().getEnabled(),
                deserializedResponse.getSensitiveWordAvoidance().getEnabled());

        assertEquals(originalResponse.getFileUpload().getEnabled(),
                deserializedResponse.getFileUpload().getEnabled());

        // Assert FileUploadConfig
        assertEquals(originalResponse.getSystemParameters().getFileSizeLimit(),
                deserializedResponse.getSystemParameters().getFileSizeLimit());
        assertEquals(originalResponse.getSystemParameters().getBatchCountLimit(),
                deserializedResponse.getSystemParameters().getBatchCountLimit());
        assertEquals(originalResponse.getSystemParameters().getImageFileSizeLimit(),
                deserializedResponse.getSystemParameters().getImageFileSizeLimit());
        assertEquals(originalResponse.getSystemParameters().getVideoFileSizeLimit(),
                deserializedResponse.getSystemParameters().getVideoFileSizeLimit());
        assertEquals(originalResponse.getSystemParameters().getAudioFileSizeLimit(),
                deserializedResponse.getSystemParameters().getAudioFileSizeLimit());
        assertEquals(originalResponse.getSystemParameters().getWorkflowFileUploadLimit(),
                deserializedResponse.getSystemParameters().getWorkflowFileUploadLimit());
    }
}
