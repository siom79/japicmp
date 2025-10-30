/*
 * Copyright (c) 2023-2025 Bradley Larrick. All rights reserved.
 *
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package japicmp.maven.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Junit 5 annotation for use with Maven plugin tests. This registers {@link LocalMojoExtension} to
 * the including test class.
 */
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(LocalMojoExtension.class)
@Target(ElementType.TYPE)
public @interface LocalMojoTest {
}
