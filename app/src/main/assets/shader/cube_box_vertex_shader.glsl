uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;

varying vec3 v_Position;
attribute vec3 a_Position;


void main() {
    vec4 postion = uProjectionMatrix * uViewMatrix * uModelMatrix * vec4(a_Position, 1.0);
    gl_Position = postion.xyww;//重点,去除位移
    v_Position = a_Position;
}