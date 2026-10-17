package com.antigravity.mobile.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antigravity.mobile.data.model.AccountProfile
import com.antigravity.mobile.data.model.decodeImagePreview

@Composable
internal fun AccountAvatar(profile: AccountProfile?, size: Dp) {
    val picture = profile?.profilePictureUrl
    val model = remember(picture) {
        if (picture?.startsWith("data:") == true) decodeImagePreview(picture) else picture?.takeIf(String::isNotBlank)
    }
    Box(Modifier.size(size).clip(CircleShape).background(Color(0xFFF2F3F5)), contentAlignment = Alignment.Center) {
        Icon(Icons.Outlined.Person, contentDescription = null, tint = SecondaryInk, modifier = Modifier.size(size * .6f))
        if (model != null) AsyncImage(model = model, contentDescription = "账号头像",
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().testTag("account-avatar"))
    }
}
