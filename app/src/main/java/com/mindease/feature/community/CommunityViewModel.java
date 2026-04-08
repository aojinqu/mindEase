package com.mindease.feature.community;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.CommunityPost;

import java.util.List;

public class CommunityViewModel extends ViewModel {

    public List<CommunityPost> loadPosts(AppContainer container, String filter) {
        return container.communityRepository.listPostsByTag(filter);
    }

    public void createPost(AppContainer container, String content, String tag) {
        container.communityRepository.createPost(content, tag);
    }
}
