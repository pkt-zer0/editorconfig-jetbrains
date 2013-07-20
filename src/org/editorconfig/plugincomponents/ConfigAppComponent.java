package org.editorconfig.plugincomponents;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.util.messages.MessageBus;
import org.editorconfig.configmanagement.EditorSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.MutablePicoContainer;

public class ConfigAppComponent implements ApplicationComponent {
    public ConfigAppComponent() {
        // Register app-level config managers, other classes
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();

        // Register EditorSettingsManager (handles stripping trailing whitespaces, newlines)
        EditorSettingsManager editorSettingsManager = new EditorSettingsManager();
        bus.connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, editorSettingsManager);
        bus.connect().subscribe(DoneSavingTopic.DONE_SAVING, editorSettingsManager);

        // Register replacement FileDocumentManager
        String fileDocumentManagerKey = FileDocumentManager.class.getName();
        ComponentConfig config = new ComponentConfig();
        config.setInterfaceClass(FileDocumentManager.class.getName());
        config.setImplementationClass(ReplacementFileDocumentManager.class.getName());
        MutablePicoContainer container = (MutablePicoContainer) ApplicationManager.getApplication().getPicoContainer();
        container.unregisterComponent(fileDocumentManagerKey);
        container.registerComponentImplementation(fileDocumentManagerKey, ReplacementFileDocumentManager.class);
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "ConfigAppComponent";
    }
}