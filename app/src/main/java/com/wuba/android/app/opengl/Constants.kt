package com.wuba.android.app.opengl

/**
 * Constant used by the program
 */
interface Constants {
    companion object {
        /** Radius of sphere for photo  */
        const val TEXTURE_SHELL_RADIUS = 2

        /** Number of sphere polygon partitions for photo, which must be an even number  */
        const val SHELL_DIVIDES = 40

        /** Maximum value that can be specified as the camera FOV variable  */
        const val CAMERA_FOV_DEGREE_MAX = 100

        /** Minimum value that can be specified as the camera FOV variable  */
        const val CAMERA_FOV_DEGREE_MIN = 30

        /** Pitch width of zoom in process  */
        const val SCALE_RATIO_TICK_EXPANSION = 1.05f

        /** Pitch width of zoom out process  */
        const val SCALE_RATIO_TICK_REDUCTION = 0.95f

        /** Rotation threshold for scroll (X axis direction)  */
        const val THRESHOLD_SCROLL_X = 0.02

        /** Rotation threshold for scroll (Y axis direction)  */
        const val THRESHOLD_SCROLL_Y = 0.02

        /** Rotation amount derivative parameter for scroll (X axis direction)  */
        const val ON_SCROLL_DIVIDER_X = 400.0f

        /** Rotation amount derivative parameter for scroll (Y axis direction)  */
        const val ON_SCROLL_DIVIDER_Y = 400.0f

        /** Movement amount derivative parameter when inertia setting is small (X axis direction)  */
        const val ON_FLING_DIVIDER_X_FOR_INERTIA_50 = 650.0f

        /** Movement amount derivative parameter when inertia setting is small (Y axis direction)  */
        const val ON_FLING_DIVIDER_Y_FOR_INERTIA_50 = 650.0f * 3.0f

        /** Movement amount derivative parameter when inertia setting is large (X axis direction)  */
        const val ON_FLING_DIVIDER_X_FOR_INERTIA_100 = 65.0f

        /** Movement amount derivative parameter when inertia setting is large (Y axis direction)  */
        const val ON_FLING_DIVIDER_Y_FOR_INERTIA_100 = 65.0f * 10.0f
    }
}