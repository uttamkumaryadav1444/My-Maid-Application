package MaidRepository.maid.model;

public enum ServiceType {
    // Cleaning & Housekeeping
    HOUSEKEEPING,       // General house cleaning
    CLEANING,           // Deep cleaning
    DISH_WASHING,       // Kitchen cleaning
    LAUNDRY,            // Washing clothes
    IRONING,            // Ironing clothes

    // Care Services
    BABYSITTING,        // Child care
    ELDER_CARE,         // Elderly care
    PATIENT_CARE,       // Patient care

    // Cooking & Kitchen
    COOKING,            // Cooking meals
    COOK,               // Alternative for cooking

    // Technical Services
    ELECTRICIAN,        // Electrical work
    PLUMBER,            // Plumbing work
    CARPENTER,          // Carpenter work
    PAINTER,            // Painting work
    AC_REPAIR,          // AC repair
    FAN_REPAIR,         // Fan repair

    // General Services
    GENERAL,            // General services
    DRIVER,             // Driving service
    GARDENER,           // Gardening
    SECURITY,           // Security guard
    OFFICE_HELPER,      // Office helper

    // Other categories
    TAILOR,             // Stitching/Tailor
    BEAUTICIAN,         // Beauty services
    SALON,              // Salon services
    MASSAGE,            // Massage therapy
    YOGA_TRAINER,       // Yoga instructor
    FITNESS_TRAINER,    // Fitness trainer

    // Default
    OTHER               // Other services
}