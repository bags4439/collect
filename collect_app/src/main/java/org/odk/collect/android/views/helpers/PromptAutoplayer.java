package org.odk.collect.android.views.helpers;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.audio.AudioHelper;
import org.odk.collect.android.audio.Clip;
import org.odk.collect.android.utilities.WidgetAppearanceUtils;

import java.util.ArrayList;
import java.util.List;

import static org.odk.collect.android.views.helpers.FormMediaHelpers.getClipID;
import static org.odk.collect.android.views.helpers.FormMediaHelpers.getPlayableAudioURI;

public class PromptAutoplayer {

    private static final String AUTOPLAY_ATTRIBUTE = "autoplay";
    private static final String AUDIO_OPTION = "audio";

    private final AudioHelper audioHelper;
    private final ReferenceManager referenceManager;

    public PromptAutoplayer(AudioHelper audioHelper, ReferenceManager referenceManager) {
        this.audioHelper = audioHelper;
        this.referenceManager = referenceManager;
    }

    public Boolean autoplayIfNeeded(FormEntryPrompt prompt) {
        String autoplayOption = prompt.getFormElement().getAdditionalAttribute(null, AUTOPLAY_ATTRIBUTE);

        if (hasAudioAutoplay(autoplayOption)) {
            List<Clip> clips = new ArrayList<>();

            addPromptAudio(prompt, clips);
            addSelectAudio(prompt, clips);

            if (clips.isEmpty()) {
                return false;
            } else {
                audioHelper.playInOrder(clips);
                return true;
            }

        } else {
            return false;
        }
    }

    private boolean hasAudioAutoplay(String autoplayOption) {
        return autoplayOption != null && autoplayOption.equalsIgnoreCase(AUDIO_OPTION);
    }

    private void addSelectAudio(FormEntryPrompt prompt, List<Clip> clips) {
        String appearance = WidgetAppearanceUtils.getSanitizedAppearanceHint(prompt);
        int controlType = prompt.getControlType();

        if ((controlType == Constants.CONTROL_SELECT_ONE) && !appearance.startsWith(WidgetAppearanceUtils.MINIMAL)
                || controlType == Constants.CONTROL_SELECT_MULTI) {

            List<SelectChoice> selectChoices = prompt.getSelectChoices();

            for (SelectChoice choice : selectChoices) {
                String selectURI = getPlayableAudioURI(prompt, choice, referenceManager);

                if (selectURI != null) {
                    clips.add(new Clip(getClipID(prompt, choice), selectURI));
                }
            }
        }
    }

    private void addPromptAudio(FormEntryPrompt prompt, List<Clip> clips) {
        String uri = getPlayableAudioURI(prompt, referenceManager);
        if (uri != null) {
            clips.add(new Clip(getClipID(prompt), getPlayableAudioURI(prompt, referenceManager)));
        }
    }
}
