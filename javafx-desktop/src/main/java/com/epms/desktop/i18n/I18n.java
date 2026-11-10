package com.epms.desktop.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * Small in-memory translation table covering 6 languages: English (en), Bangla (bn),
 * Spanish (es), Hindi (hi), French (fr), and Chinese (zh). Call I18n.t("some.key")
 * anywhere in the UI to get the text in the currently selected language. Change
 * I18n.lang and re-show the current screen to switch the whole application's
 * language at runtime. If a translation is missing for the active language, the
 * English value is used as a safe fallback.
 */
public class I18n {

    public static final String[] LANGS = {"en", "bn", "es", "hi", "fr", "zh"};

    public static String lang = "en";

    private static final Map<String, String[]> TABLE = new HashMap<>();

    private static void put(String key, String en, String bn, String es, String hi, String fr, String zh) {
        TABLE.put(key, new String[]{en, bn, es, hi, fr, zh});
    }

    private static int indexOf(String code) {
        for (int i = 0; i < LANGS.length; i++) if (LANGS[i].equals(code)) return i;
        return 0;
    }

    public static String t(String key) {
        String[] values = TABLE.get(key);
        if (values == null) return key;
        String v = values[indexOf(lang)];
        return (v != null && !v.isEmpty()) ? v : values[0];
    }

    static {
        // ---- Navigation menu ----
        put("menu.dashboard", "Dashboard", "ড্যাশবোর্ড", "Panel", "डैशबोर्ड", "Tableau de bord", "仪表盘");
        put("menu.employees", "Employees", "কর্মচারী", "Empleados", "कर्मचारी", "Employés", "员工");
        put("menu.payroll", "Payroll Calculator", "পেরোল ক্যালকুলেটর", "Calculadora de Nómina", "पेरोल कैलकुलेटर", "Calculateur de Paie", "薪资计算器");
        put("menu.timeclock", "Time Clock", "টাইম ক্লক", "Reloj de Trabajo", "टाइम क्लॉक", "Pointeuse", "打卡钟");
        put("menu.payslips", "Payslips", "পে-স্লিপ", "Recibos de Pago", "पे-स्लिप", "Fiches de Paie", "工资单");
        put("menu.reports", "Reports", "রিপোর্ট", "Informes", "रिपोर्ट", "Rapports", "报表");
        put("menu.help", "Help", "সাহায্য", "Ayuda", "सहायता", "Aide", "帮助");
        put("menu.settings", "Settings", "সেটিংস", "Configuración", "सेटिंग्स", "Paramètres", "设置");
        put("menu.logout", "Log Out", "লগ আউট", "Cerrar Sesión", "लॉग आउट", "Déconnexion", "退出登录");

        // ---- Common buttons ----
        put("common.back", "BACK", "ফিরে যান", "Atrás", "वापस", "Retour", "返回");
        put("common.save", "SAVE", "সংরক্ষণ করুন", "Guardar", "सेव करें", "Enregistrer", "保存");
        put("common.close", "CLOSE", "বন্ধ করুন", "Cerrar", "बंद करें", "Fermer", "关闭");
        put("common.refresh", "REFRESH", "রিফ্রেশ", "Actualizar", "रीफ्रेश करें", "Actualiser", "刷新");

        // ---- Dashboard ----
        put("dashboard.welcome", "Welcome to EPMS", "EPMS-এ স্বাগতম", "Bienvenido a EPMS", "EPMS में आपका स्वागत है", "Bienvenue sur EPMS", "欢迎使用 EPMS");
        put("dashboard.subtitle", "Enterprise Payroll Management System", "এন্টারপ্রাইজ পেরোল ম্যানেজমেন্ট সিস্টেম",
                "Sistema Empresarial de Gestión de Nómina", "एंटरप्राइज़ पेरोल मैनेजमेंट सिस्टम",
                "Système de Gestion de Paie d'Entreprise", "企业薪资管理系统");
        put("dashboard.hint",
                "Use the menu on the left to manage employees, run payroll, track time, "
                        + "review payslips and reports, or open Help for a full walkthrough.",
                "কর্মচারী পরিচালনা, পেরোল চালানো, সময় ট্র্যাক করা, পে-স্লিপ ও রিপোর্ট দেখতে বাম পাশের "
                        + "মেনু ব্যবহার করুন, অথবা সম্পূর্ণ গাইডের জন্য সাহায্য (Help) খুলুন।",
                "Use el menú de la izquierda para administrar empleados, procesar la nómina, registrar "
                        + "el tiempo, revisar recibos de pago e informes, o abra Ayuda para ver una guía completa.",
                "कर्मचारियों को प्रबंधित करने, पेरोल चलाने, समय ट्रैक करने, पे-स्लिप और रिपोर्ट देखने के लिए बाईं "
                        + "ओर के मेनू का उपयोग करें, या पूरी जानकारी के लिए सहायता (Help) खोलें।",
                "Utilisez le menu de gauche pour gérer les employés, exécuter la paie, suivre le temps, "
                        + "consulter les fiches de paie et les rapports, ou ouvrez l'Aide pour un guide complet.",
                "使用左侧菜单管理员工、运行薪资、记录工时、查看工资单和报表，或打开帮助获取完整指南。");

        // ---- Settings ----
        put("settings.title", "Settings", "সেটিংস", "Configuración", "सेटिंग्स", "Paramètres", "设置");
        put("settings.language.section", "Language", "ভাষা", "Idioma", "भाषा", "Langue", "语言");
        put("settings.language.hint",
                "Choose the language used across the menu, Help Center, and screen labels.",
                "মেনু, সাহায্য কেন্দ্র এবং স্ক্রিনের লেবেলে যে ভাষা ব্যবহার হবে তা বেছে নিন।",
                "Elija el idioma que se usará en el menú, el Centro de Ayuda y las etiquetas de pantalla.",
                "मेनू, सहायता केंद्र और स्क्रीन लेबल में उपयोग होने वाली भाषा चुनें।",
                "Choisissez la langue utilisée dans le menu, le Centre d'Aide et les libellés des écrans.",
                "选择菜单、帮助中心和屏幕标签中使用的语言。");
        put("settings.language.en", "English", "English", "English", "English", "English", "English");
        put("settings.language.bn", "বাংলা (Bangla)", "বাংলা (Bangla)", "বাংলা (Bangla)", "বাংলা (Bangla)", "বাংলা (Bangla)", "বাংলা (Bangla)");
        put("settings.language.es", "Español", "Español", "Español", "Español", "Español", "Español");
        put("settings.language.hi", "हिन्दी", "हिन्दी", "हिन्दी", "हिन्दी", "हिन्दी", "हिन्दी");
        put("settings.language.fr", "Français", "Français", "Français", "Français", "Français", "Français");
        put("settings.language.zh", "中文 (Chinese)", "中文 (Chinese)", "中文 (Chinese)", "中文 (Chinese)", "中文 (Chinese)", "中文 (Chinese)");
        put("settings.apply", "APPLY LANGUAGE", "ভাষা প্রয়োগ করুন", "Aplicar idioma", "भाषा लागू करें", "Appliquer la langue", "应用语言");
        put("settings.connection.section", "Server Connection", "সার্ভার সংযোগ", "Conexión al Servidor", "सर्वर कनेक्शन", "Connexion au Serveur", "服务器连接");
        put("settings.connection.hint",
                "The desktop app talks to the payroll backend at the address below.",
                "ডেস্কটপ অ্যাপটি নিচের অ্যাড্রেসে পেরোল ব্যাকএন্ডের সাথে যোগাযোগ করে।",
                "La aplicación de escritorio se comunica con el backend de nómina en la siguiente dirección.",
                "डेस्कटॉप ऐप नीचे दिए गए पते पर पेरोल बैकएंड से संपर्क करता है।",
                "L'application de bureau communique avec le backend de paie à l'adresse ci-dessous.",
                "桌面应用程序通过以下地址与薪资后端通信。");
        put("settings.about.section", "About", "সম্পর্কে", "Acerca de", "बारे में", "À propos", "关于");
        put("settings.about.text",
                "EPMS Desktop — JavaFX client for the Enterprise Payroll Management System.",
                "EPMS ডেস্কটপ — এন্টারপ্রাইজ পেরোল ম্যানেজমেন্ট সিস্টেমের জাভাFX ক্লায়েন্ট।",
                "EPMS Desktop — cliente JavaFX para el Sistema Empresarial de Gestión de Nómina.",
                "EPMS डेस्कटॉप — एंटरप्राइज़ पेरोल मैनेजमेंट सिस्टम के लिए JavaFX क्लाइंट।",
                "EPMS Desktop — client JavaFX pour le Système de Gestion de Paie d'Entreprise.",
                "EPMS 桌面版 — 企业薪资管理系统的 JavaFX 客户端。");

        // ---- Time Clock ----
        put("timeclock.title", "Time Clock", "টাইম ক্লক", "Reloj de Trabajo", "टाइम क्लॉक", "Pointeuse", "打卡钟");
        put("timeclock.select", "Select Employee", "কর্মচারী বেছে নিন", "Seleccionar Empleado", "कर्मचारी चुनें", "Sélectionner un Employé", "选择员工");
        put("timeclock.clockin", "CLOCK IN", "ক্লক ইন", "Entrada", "क्लॉक इन", "Pointer l'arrivée", "上班打卡");
        put("timeclock.clockout", "CLOCK OUT", "ক্লক আউট", "Salida", "क्लॉक आउट", "Pointer le départ", "下班打卡");
        put("timeclock.status.open", "Currently clocked in", "এখন ক্লক-ইন অবস্থায় আছে", "Actualmente registrado (dentro)",
                "अभी क्लॉक-इन है", "Actuellement pointé (présent)", "当前已打卡在岗");
        put("timeclock.status.idle", "Not clocked in", "ক্লক-ইন করা নেই", "No ha registrado entrada", "क्लॉक-इन नहीं है",
                "Non pointé", "尚未打卡");
        put("timeclock.history", "Recent Time Entries", "সাম্প্রতিক টাইম এন্ট্রি", "Registros Recientes",
                "हाल की टाइम एंट्री", "Pointages Récents", "最近打卡记录");
        put("timeclock.col.date", "Date", "তারিখ", "Fecha", "तारीख", "Date", "日期");
        put("timeclock.col.in", "Clock In", "ক্লক ইন", "Entrada", "क्लॉक इन", "Arrivée", "上班时间");
        put("timeclock.col.out", "Clock Out", "ক্লক আউট", "Salida", "क्लॉक आउट", "Départ", "下班时间");
        put("timeclock.col.regular", "Regular Hrs", "নিয়মিত ঘণ্টা", "Horas Regulares", "नियमित घंटे", "Heures Normales", "正常工时");
        put("timeclock.col.overtime", "Overtime Hrs", "ওভারটাইম ঘণ্টা", "Horas Extra", "ओवरटाइम घंटे", "Heures Supplémentaires", "加班工时");
        put("timeclock.col.status", "Status", "অবস্থা", "Estado", "स्थिति", "Statut", "状态");

        // ---- Payslips ----
        put("payslips.title", "Payslips", "পে-স্লিপ", "Recibos de Pago", "पे-स्लिप", "Fiches de Paie", "工资单");
        put("payslips.select", "Select Employee", "কর্মচারী বেছে নিন", "Seleccionar Empleado", "कर्मचारी चुनें", "Sélectionner un Employé", "选择员工");
        put("payslips.empty",
                "No saved payslips yet for this employee. Go to Payroll Calculator, calculate a "
                        + "payroll, then click SAVE PAYSLIP to create one.",
                "এই কর্মচারীর জন্য এখনো কোনো পে-স্লিপ সংরক্ষিত নেই। পেরোল ক্যালকুলেটরে গিয়ে হিসাব করুন, "
                        + "তারপর SAVE PAYSLIP চাপুন।",
                "Aún no hay recibos de pago guardados para este empleado. Vaya a la Calculadora de "
                        + "Nómina, calcule una nómina y luego haga clic en GUARDAR RECIBO para crear uno.",
                "इस कर्मचारी के लिए अभी तक कोई पे-स्लिप सेव नहीं है। पेरोल कैलकुलेटर में जाएं, पेरोल कैलकुलेट "
                        + "करें, फिर SAVE PAYSLIP पर क्लिक करें।",
                "Aucune fiche de paie enregistrée pour cet employé. Allez dans le Calculateur de Paie, "
                        + "calculez une paie, puis cliquez sur ENREGISTRER LA FICHE pour en créer une.",
                "该员工目前还没有已保存的工资单。请前往薪资计算器，计算一次薪资，然后点击“保存工资单”来创建一条记录。");
        put("payslips.col.period", "Pay Period", "পে পিরিয়ড", "Período de Pago", "पे पीरियड", "Période de Paie", "薪资周期");
        put("payslips.col.paydate", "Pay Date", "পে-ডেট", "Fecha de Pago", "पे-डेट", "Date de Paie", "发薪日");
        put("payslips.col.gross", "Gross", "গ্রস", "Bruto", "ग्रॉस", "Brut", "总额");
        put("payslips.col.net", "Net", "নেট", "Neto", "नेट", "Net", "净额");
        put("payslips.detail.title", "Payslip Detail", "পে-স্লিপের বিস্তারিত", "Detalle del Recibo", "पे-स्लिप विवरण",
                "Détail de la Fiche de Paie", "工资单详情");
        put("payslips.detail.gross", "Gross Pay", "গ্রস পে", "Pago Bruto", "ग्रॉस पे", "Salaire Brut", "总工资");
        put("payslips.detail.federal", "Federal Tax", "ফেডারেল ট্যাক্স", "Impuesto Federal", "फेडरल टैक्स", "Impôt Fédéral", "联邦税");
        put("payslips.detail.ss", "Social Security", "সোশ্যাল সিকিউরিটি", "Seguro Social", "सोशल सिक्योरिटी", "Sécurité Sociale", "社会保障");
        put("payslips.detail.medicare", "Medicare", "মেডিকেয়ার", "Medicare", "मेडिकेयर", "Medicare", "医疗保险");
        put("payslips.detail.pa", "State (PA) Tax", "স্টেট (PA) ট্যাক্স", "Impuesto Estatal (PA)", "स्टेट (PA) टैक्स",
                "Impôt d'État (PA)", "州税（PA）");
        put("payslips.detail.local", "Local Tax", "লোকাল ট্যাক্স", "Impuesto Local", "लोकल टैक्स", "Impôt Local", "地方税");
        put("payslips.detail.lst", "LST", "LST", "LST", "LST", "LST", "LST");
        put("payslips.detail.sui", "SUI", "SUI", "SUI", "SUI", "SUI", "SUI");
        put("payslips.detail.health", "Health Deduction", "হেলথ ডিডাকশন", "Deducción de Salud", "हेल्थ डिडक्शन",
                "Déduction Santé", "医疗扣除");
        put("payslips.detail.k401", "401(k)", "৪০১(k)", "401(k)", "401(k)", "401(k)", "401(k)");
        put("payslips.detail.roth", "Roth", "রথ", "Roth", "रॉथ", "Roth", "Roth");
        put("payslips.detail.match", "Employer Match", "এমপ্লয়ার ম্যাচ", "Aporte del Empleador", "एम्प्लॉयर मैच",
                "Contribution de l'Employeur", "雇主匹配");
        put("payslips.detail.net", "NET PAY", "নেট পে", "Pago Neto", "नेट पे", "Salaire Net", "净工资");

        // ---- Reports ----
        put("reports.title", "Reports", "রিপোর্ট", "Informes", "रिपोर्ट", "Rapports", "报表");
        put("reports.headcount", "Total Employees", "মোট কর্মচারী", "Total de Empleados", "कुल कर्मचारी", "Total des Employés", "员工总数");
        put("reports.active", "Active Employees", "সক্রিয় কর্মচারী", "Empleados Activos", "सक्रिय कर्मचारी", "Employés Actifs", "在职员工");
        put("reports.inactive", "Inactive Employees", "নিষ্ক্রিয় কর্মচারী", "Empleados Inactivos", "निष्क्रिय कर्मचारी",
                "Employés Inactifs", "非在职员工");
        put("reports.avgrate", "Average Hourly Rate", "গড় ঘণ্টাপ্রতি রেট", "Tarifa Media por Hora", "औसत प्रति घंटा दर",
                "Taux Horaire Moyen", "平均时薪");
        put("reports.bydept", "Headcount by Department", "বিভাগ অনুযায়ী কর্মচারী সংখ্যা", "Personal por Departamento",
                "विभाग अनुसार कर्मचारी संख्या", "Effectif par Département", "各部门人数");
        put("reports.col.dept", "Department", "বিভাগ", "Departamento", "विभाग", "Département", "部门");
        put("reports.col.count", "Employees", "কর্মচারী সংখ্যা", "Empleados", "कर्मचारी", "Employés", "员工数");

        // ---- Help ----
        put("help.title", "Help Center", "সাহায্য কেন্দ্র", "Centro de Ayuda", "सहायता केंद्र", "Centre d'Aide", "帮助中心");
        put("help.language", "Language:", "ভাষা:", "Idioma:", "भाषा:", "Langue :", "语言：");

        put("help.topic.overview", "Overview", "সংক্ষিপ্ত বিবরণ", "Resumen", "सारांश", "Aperçu", "概览");
        put("help.topic.employees", "Employees", "কর্মচারী", "Empleados", "कर्मचारी", "Employés", "员工");
        put("help.topic.payroll", "Payroll Calculator", "পেরোল ক্যালকুলেটর", "Calculadora de Nómina", "पेरोल कैलकुलेटर",
                "Calculateur de Paie", "薪资计算器");
        put("help.topic.timeclock", "Time Clock", "টাইম ক্লক", "Reloj de Trabajo", "टाइम क्लॉक", "Pointeuse", "打卡钟");
        put("help.topic.payslips", "Payslips", "পে-স্লিপ", "Recibos de Pago", "पे-स्लिप", "Fiches de Paie", "工资单");
        put("help.topic.reports", "Reports", "রিপোর্ট", "Informes", "रिपोर्ट", "Rapports", "报表");
        put("help.topic.settings", "Settings & Language", "সেটিংস ও ভাষা", "Configuración e Idioma", "सेटिंग्स और भाषा",
                "Paramètres et Langue", "设置与语言");

        put("help.overview.body",
                "EPMS (Enterprise Payroll Management System) is a small desktop application that "
                        + "talks to a Spring Boot backend to manage employees and run payroll for a "
                        + "single company.\n\n"
                        + "The normal workflow is:\n"
                        + "1. Add employees in the Employees screen (pay rate, department, deductions).\n"
                        + "2. Have employees clock in and out from the Time Clock screen during the day.\n"
                        + "3. At the end of a pay period, open Payroll Calculator, pick the employee, "
                        + "confirm hours and rates, and calculate the pay.\n"
                        + "4. Click SAVE PAYSLIP to store that calculation permanently.\n"
                        + "5. The saved result appears immediately under Payslips for that employee, "
                        + "and rolls into the numbers shown on the Reports screen.\n\n"
                        + "Every screen has a BACK button that returns to this Dashboard, and the app "
                        + "remembers your language choice from the Settings screen while it is running.",
                "EPMS (এন্টারপ্রাইজ পেরোল ম্যানেজমেন্ট সিস্টেম) হলো একটি ছোট ডেস্কটপ অ্যাপ্লিকেশন, যা একটি "
                        + "Spring Boot ব্যাকএন্ডের সাথে যোগাযোগ করে একটি কোম্পানির কর্মচারী ও পেরোল পরিচালনা করে।\n\n"
                        + "সাধারণ কর্মপ্রবাহ (workflow) এই রকম:\n"
                        + "১. Employees স্ক্রিনে কর্মচারী যুক্ত করুন (রেট, বিভাগ, ডিডাকশন)।\n"
                        + "২. দিনের মধ্যে কর্মচারীরা Time Clock স্ক্রিন থেকে ক্লক-ইন/ক্লক-আউট করবে।\n"
                        + "৩. পে-পিরিয়ড শেষে Payroll Calculator খুলুন, কর্মচারী বেছে নিন, ঘণ্টা ও রেট "
                        + "নিশ্চিত করে হিসাব করুন।\n"
                        + "৪. SAVE PAYSLIP চাপুন যাতে হিসাবটি স্থায়ীভাবে সংরক্ষিত হয়।\n"
                        + "৫. সংরক্ষিত ফলাফল সাথে সাথে সেই কর্মচারীর Payslips-এ দেখা যাবে, এবং Reports "
                        + "স্ক্রিনের সংখ্যাগুলোতেও যুক্ত হবে।\n\n"
                        + "প্রতিটি স্ক্রিনে একটি BACK বাটন আছে যা এই ড্যাশবোর্ডে ফিরিয়ে আনে, এবং Settings "
                        + "থেকে বেছে নেওয়া ভাষা অ্যাপ চলা অবস্থায় মনে রাখা হয়।",
                "EPMS (Sistema Empresarial de Gestión de Nómina) es una pequeña aplicación de "
                        + "escritorio que se comunica con un backend en Spring Boot para administrar "
                        + "empleados y procesar la nómina de una sola empresa.\n\n"
                        + "El flujo de trabajo normal es:\n"
                        + "1. Agregue empleados en la pantalla de Empleados (tarifa de pago, departamento, deducciones).\n"
                        + "2. Haga que los empleados registren su entrada y salida desde la pantalla del "
                        + "Reloj de Trabajo durante el día.\n"
                        + "3. Al final de un período de pago, abra la Calculadora de Nómina, seleccione "
                        + "al empleado, confirme las horas y tarifas, y calcule el pago.\n"
                        + "4. Haga clic en GUARDAR RECIBO para almacenar ese cálculo de forma permanente.\n"
                        + "5. El resultado guardado aparece inmediatamente en Recibos de Pago para ese "
                        + "empleado, y se incluye en los números que se muestran en Informes.\n\n"
                        + "Cada pantalla tiene un botón ATRÁS que regresa a este Panel, y la aplicación "
                        + "recuerda su elección de idioma desde Configuración mientras se ejecuta.",
                "EPMS (एंटरप्राइज़ पेरोल मैनेजमेंट सिस्टम) एक छोटा डेस्कटॉप एप्लिकेशन है जो एक कंपनी के "
                        + "कर्मचारियों और पेरोल को प्रबंधित करने के लिए Spring Boot बैकएंड से बात करता है।\n\n"
                        + "सामान्य वर्कफ़्लो इस प्रकार है:\n"
                        + "1. Employees स्क्रीन में कर्मचारी जोड़ें (पे रेट, विभाग, डिडक्शन)।\n"
                        + "2. दिन के दौरान कर्मचारी Time Clock स्क्रीन से क्लॉक-इन और क्लॉक-आउट करें।\n"
                        + "3. पे-पीरियड के अंत में, Payroll Calculator खोलें, कर्मचारी चुनें, घंटे और रेट "
                        + "की पुष्टि करें, और पे कैलकुलेट करें।\n"
                        + "4. इस कैलकुलेशन को स्थायी रूप से सेव करने के लिए SAVE PAYSLIP पर क्लिक करें।\n"
                        + "5. सेव किया गया परिणाम तुरंत उस कर्मचारी के Payslips में दिखता है, और Reports "
                        + "स्क्रीन पर दिखाई गई संख्याओं में भी शामिल हो जाता है।\n\n"
                        + "हर स्क्रीन पर एक BACK बटन है जो इस डैशबोर्ड पर वापस लाता है, और ऐप चलते समय "
                        + "Settings स्क्रीन से चुनी गई भाषा को याद रखता है।",
                "EPMS (Système de Gestion de Paie d'Entreprise) est une petite application de bureau "
                        + "qui communique avec un backend Spring Boot pour gérer les employés et exécuter "
                        + "la paie d'une seule entreprise.\n\n"
                        + "Le flux de travail normal est le suivant :\n"
                        + "1. Ajoutez des employés dans l'écran Employés (taux de rémunération, département, déductions).\n"
                        + "2. Faites pointer les employés à l'arrivée et au départ depuis l'écran "
                        + "Pointeuse pendant la journée.\n"
                        + "3. À la fin d'une période de paie, ouvrez le Calculateur de Paie, sélectionnez "
                        + "l'employé, confirmez les heures et les taux, puis calculez la paie.\n"
                        + "4. Cliquez sur ENREGISTRER LA FICHE pour stocker définitivement ce calcul.\n"
                        + "5. Le résultat enregistré apparaît immédiatement dans les Fiches de Paie de "
                        + "cet employé, et s'intègre aux chiffres affichés dans Rapports.\n\n"
                        + "Chaque écran dispose d'un bouton RETOUR qui revient à ce Tableau de bord, et "
                        + "l'application se souvient de votre choix de langue depuis Paramètres pendant son exécution.",
                "EPMS（企业薪资管理系统）是一个小型桌面应用程序，通过与 Spring Boot 后端通信来管理一家公司的员工和薪资。\n\n"
                        + "正常的工作流程如下：\n"
                        + "1. 在“员工”界面中添加员工（薪率、部门、扣款项）。\n"
                        + "2. 让员工在一天中通过“打卡钟”界面上下班打卡。\n"
                        + "3. 在薪资周期结束时，打开“薪资计算器”，选择员工，确认工时和薪率，然后计算薪资。\n"
                        + "4. 点击“保存工资单”以永久保存该计算结果。\n"
                        + "5. 保存的结果会立即显示在该员工的“工资单”中，并计入“报表”界面所显示的数字。\n\n"
                        + "每个界面都有一个“返回”按钮可回到此仪表盘，应用程序在运行期间会记住您在“设置”界面中选择的语言。");

        put("help.employees.body",
                "The Employees screen lists everyone in the system and lets you add, update, or "
                        + "remove a record.\n\n"
                        + "• Use the search box to filter by name or employee number.\n"
                        + "• Click a row in the table to load that person's details into the form "
                        + "below it.\n"
                        + "• ADD / SAVE creates a brand-new employee from the form.\n"
                        + "• UPDATE overwrites the currently selected employee with the form's values.\n"
                        + "• DELETE removes the selected employee permanently.\n"
                        + "• CLEAR empties the form so you can start a new entry.",
                "Employees স্ক্রিনে সবার তালিকা দেখা যায় এবং নতুন রেকর্ড যুক্ত, আপডেট বা মুছে ফেলা যায়।\n\n"
                        + "• নাম বা এমপ্লয়ি নম্বর দিয়ে খুঁজতে সার্চ বক্স ব্যবহার করুন।\n"
                        + "• টেবিলের কোনো সারিতে ক্লিক করলে সেই কর্মচারীর তথ্য নিচের ফর্মে চলে আসবে।\n"
                        + "• ADD / SAVE ফর্ম থেকে একদম নতুন কর্মচারী তৈরি করে।\n"
                        + "• UPDATE বর্তমানে নির্বাচিত কর্মচারীর তথ্য ফর্মের মান দিয়ে বদলে দেয়।\n"
                        + "• DELETE নির্বাচিত কর্মচারীকে স্থায়ীভাবে মুছে দেয়।\n"
                        + "• CLEAR ফর্মটি খালি করে দেয় যাতে নতুন এন্ট্রি শুরু করা যায়।",
                "La pantalla de Empleados enumera a todas las personas del sistema y le permite "
                        + "agregar, actualizar o eliminar un registro.\n\n"
                        + "• Use el cuadro de búsqueda para filtrar por nombre o número de empleado.\n"
                        + "• Haga clic en una fila de la tabla para cargar los datos de esa persona en "
                        + "el formulario de abajo.\n"
                        + "• AGREGAR / GUARDAR crea un empleado completamente nuevo a partir del formulario.\n"
                        + "• ACTUALIZAR sobrescribe al empleado actualmente seleccionado con los valores del formulario.\n"
                        + "• ELIMINAR quita al empleado seleccionado de forma permanente.\n"
                        + "• LIMPIAR vacía el formulario para que pueda comenzar un nuevo registro.",
                "Employees स्क्रीन सिस्टम में मौजूद सभी लोगों को सूचीबद्ध करती है और आपको रेकॉर्ड जोड़ने, "
                        + "अपडेट करने या हटाने देती है।\n\n"
                        + "• नाम या कर्मचारी नंबर से फ़िल्टर करने के लिए सर्च बॉक्स का उपयोग करें।\n"
                        + "• टेबल की किसी पंक्ति पर क्लिक करने से उस व्यक्ति का विवरण नीचे के फॉर्म में आ जाता है।\n"
                        + "• ADD / SAVE फॉर्म से एक बिल्कुल नया कर्मचारी बनाता है।\n"
                        + "• UPDATE चुने गए कर्मचारी को फॉर्म के मानों से बदल देता है।\n"
                        + "• DELETE चुने गए कर्मचारी को स्थायी रूप से हटा देता है।\n"
                        + "• CLEAR फॉर्म को खाली कर देता है ताकि आप नई एंट्री शुरू कर सकें।",
                "L'écran Employés répertorie toutes les personnes du système et vous permet "
                        + "d'ajouter, de modifier ou de supprimer un enregistrement.\n\n"
                        + "• Utilisez la zone de recherche pour filtrer par nom ou numéro d'employé.\n"
                        + "• Cliquez sur une ligne du tableau pour charger les détails de cette personne "
                        + "dans le formulaire ci-dessous.\n"
                        + "• AJOUTER / ENREGISTRER crée un nouvel employé à partir du formulaire.\n"
                        + "• METTRE À JOUR remplace l'employé actuellement sélectionné par les valeurs du formulaire.\n"
                        + "• SUPPRIMER retire définitivement l'employé sélectionné.\n"
                        + "• EFFACER vide le formulaire afin que vous puissiez commencer une nouvelle saisie.",
                "员工界面列出系统中的所有人员，并允许您添加、更新或删除记录。\n\n"
                        + "• 使用搜索框按姓名或员工编号进行筛选。\n"
                        + "• 点击表格中的一行，即可将该员工的详细信息加载到下方表单中。\n"
                        + "• “添加/保存”会根据表单内容创建一名全新员工。\n"
                        + "• “更新”会用表单中的值覆盖当前选中的员工记录。\n"
                        + "• “删除”会永久移除选中的员工。\n"
                        + "• “清空”会清空表单，以便您开始新的录入。");

        put("help.payroll.body",
                "Payroll Calculator computes one pay period for one employee using the tax and "
                        + "deduction rates you type in.\n\n"
                        + "1. Pick the employee from the dropdown.\n"
                        + "2. Adjust hours and rates if this period is different from the defaults.\n"
                        + "3. Click CALCULATE to preview Gross, Federal Taxable Wages, and Net Pay — "
                        + "this preview is not stored anywhere yet.\n"
                        + "4. Click SAVE PAYSLIP once you are happy with the numbers — this permanently "
                        + "records the payroll, and it will immediately show up on the Payslips screen "
                        + "for that employee.",
                "Payroll Calculator একজন কর্মচারীর জন্য একটি পে-পিরিয়ড হিসাব করে, আপনি টাইপ করা ট্যাক্স ও "
                        + "ডিডাকশন রেট দিয়ে।\n\n"
                        + "১. ড্রপডাউন থেকে কর্মচারী বেছে নিন।\n"
                        + "২. এই পিরিয়ডের জন্য ঘণ্টা ও রেট প্রয়োজনে বদলে দিন।\n"
                        + "৩. CALCULATE চাপুন — Gross, Federal Taxable Wages, এবং Net Pay দেখা যাবে; এই "
                        + "প্রিভিউ এখনও সংরক্ষিত হয়নি।\n"
                        + "৪. সংখ্যা ঠিক থাকলে SAVE PAYSLIP চাপুন — এটি স্থায়ীভাবে সংরক্ষিত হবে এবং সাথে সাথে "
                        + "সেই কর্মচারীর Payslips-এ দেখা যাবে।",
                "La Calculadora de Nómina calcula un período de pago para un empleado usando las "
                        + "tasas de impuestos y deducciones que usted ingresa.\n\n"
                        + "1. Seleccione al empleado en el menú desplegable.\n"
                        + "2. Ajuste las horas y tarifas si este período es diferente de los valores predeterminados.\n"
                        + "3. Haga clic en CALCULAR para ver una vista previa del Pago Bruto, el Salario "
                        + "Federal Imponible y el Pago Neto — esta vista previa aún no se guarda.\n"
                        + "4. Haga clic en GUARDAR RECIBO cuando esté satisfecho con los números — esto "
                        + "registra la nómina de forma permanente y aparecerá inmediatamente en Recibos "
                        + "de Pago de ese empleado.",
                "Payroll Calculator आपके द्वारा दर्ज किए गए टैक्स और डिडक्शन दरों का उपयोग करके एक कर्मचारी "
                        + "के लिए एक पे-पीरियड की गणना करता है।\n\n"
                        + "1. ड्रॉपडाउन से कर्मचारी चुनें।\n"
                        + "2. यदि यह पीरियड डिफ़ॉल्ट से अलग है तो घंटे और दरें समायोजित करें।\n"
                        + "3. Gross, Federal Taxable Wages और Net Pay का पूर्वावलोकन देखने के लिए CALCULATE "
                        + "पर क्लिक करें — यह पूर्वावलोकन अभी सेव नहीं होता।\n"
                        + "4. संख्याओं से संतुष्ट होने पर SAVE PAYSLIP पर क्लिक करें — यह पेरोल को स्थायी रूप "
                        + "से रिकॉर्ड करता है और उस कर्मचारी के Payslips स्क्रीन पर तुरंत दिखाई देगा।",
                "Le Calculateur de Paie calcule une période de paie pour un employé à l'aide des taux "
                        + "d'impôts et de déductions que vous saisissez.\n\n"
                        + "1. Sélectionnez l'employé dans le menu déroulant.\n"
                        + "2. Ajustez les heures et les taux si cette période diffère des valeurs par défaut.\n"
                        + "3. Cliquez sur CALCULER pour prévisualiser le Salaire Brut, le Salaire Fédéral "
                        + "Imposable et le Salaire Net — cet aperçu n'est pas encore enregistré.\n"
                        + "4. Cliquez sur ENREGISTRER LA FICHE une fois satisfait des chiffres — cela "
                        + "enregistre définitivement la paie, qui apparaîtra immédiatement dans les "
                        + "Fiches de Paie de cet employé.",
                "薪资计算器会根据您输入的税率和扣款比例，为一名员工计算一个薪资周期的薪资。\n\n"
                        + "1. 从下拉列表中选择员工。\n"
                        + "2. 如果本周期与默认值不同，请调整工时和薪率。\n"
                        + "3. 点击“计算”以预览总工资、联邦应税工资和净工资 — 此预览尚未被保存。\n"
                        + "4. 确认数字无误后点击“保存工资单” — 这会永久记录该笔薪资，并立即显示在该员工的工资单界面中。");

        put("help.timeclock.body",
                "The Time Clock screen records when an employee starts and finishes work.\n\n"
                        + "• Select the employee — the screen tells you whether they are currently "
                        + "clocked in.\n"
                        + "• CLOCK IN starts a new entry with the current time.\n"
                        + "• CLOCK OUT closes the open entry and automatically works out regular hours "
                        + "(up to 8 per day) and overtime hours (anything beyond 8).\n"
                        + "• The table below always shows the employee's most recent entries.",
                "Time Clock স্ক্রিনে একজন কর্মচারী কখন কাজ শুরু ও শেষ করেছে তা রেকর্ড করা হয়।\n\n"
                        + "• কর্মচারী বেছে নিন — স্ক্রিনে দেখানো হবে তিনি এখন ক্লক-ইন অবস্থায় আছেন কিনা।\n"
                        + "• CLOCK IN বর্তমান সময় দিয়ে একটি নতুন এন্ট্রি শুরু করে।\n"
                        + "• CLOCK OUT খোলা এন্ট্রিটি বন্ধ করে এবং স্বয়ংক্রিয়ভাবে নিয়মিত ঘণ্টা (দিনে ৮ ঘণ্টা "
                        + "পর্যন্ত) এবং ওভারটাইম ঘণ্টা (৮ ঘণ্টার বেশি) হিসাব করে।\n"
                        + "• নিচের টেবিলে সেই কর্মচারীর সাম্প্রতিক এন্ট্রিগুলো দেখা যায়।",
                "La pantalla del Reloj de Trabajo registra cuándo un empleado comienza y termina de trabajar.\n\n"
                        + "• Seleccione al empleado — la pantalla le indica si actualmente tiene la entrada registrada.\n"
                        + "• ENTRADA inicia un nuevo registro con la hora actual.\n"
                        + "• SALIDA cierra el registro abierto y calcula automáticamente las horas "
                        + "regulares (hasta 8 por día) y las horas extra (todo lo que exceda de 8).\n"
                        + "• La tabla de abajo siempre muestra los registros más recientes del empleado.",
                "Time Clock स्क्रीन यह रिकॉर्ड करती है कि कोई कर्मचारी कब काम शुरू और खत्म करता है।\n\n"
                        + "• कर्मचारी चुनें — स्क्रीन बताएगी कि वे अभी क्लॉक-इन हैं या नहीं।\n"
                        + "• CLOCK IN वर्तमान समय के साथ एक नई एंट्री शुरू करता है।\n"
                        + "• CLOCK OUT खुली एंट्री को बंद करता है और स्वचालित रूप से नियमित घंटे (प्रतिदिन 8 "
                        + "तक) और ओवरटाइम घंटे (8 से अधिक कुछ भी) निकालता है।\n"
                        + "• नीचे की टेबल हमेशा कर्मचारी की सबसे हाल की एंट्री दिखाती है।",
                "L'écran Pointeuse enregistre l'heure à laquelle un employé commence et termine son travail.\n\n"
                        + "• Sélectionnez l'employé — l'écran indique s'il est actuellement pointé.\n"
                        + "• POINTER L'ARRIVÉE démarre un nouveau pointage avec l'heure actuelle.\n"
                        + "• POINTER LE DÉPART clôture le pointage ouvert et calcule automatiquement les "
                        + "heures normales (jusqu'à 8 par jour) et les heures supplémentaires (au-delà de 8).\n"
                        + "• Le tableau ci-dessous affiche toujours les pointages les plus récents de l'employé.",
                "打卡钟界面记录员工的上下班时间。\n\n"
                        + "• 选择员工 — 界面会显示该员工当前是否已打卡在岗。\n"
                        + "• “上班打卡”会以当前时间开始一条新记录。\n"
                        + "• “下班打卡”会关闭当前打开的记录，并自动计算正常工时（每天最多8小时）和加班工时（超过8小时的部分）。\n"
                        + "• 下方表格始终显示该员工最近的打卡记录。");

        put("help.payslips.body",
                "Payslips shows every payroll that has actually been saved (not just previewed) "
                        + "for the selected employee.\n\n"
                        + "• Pick an employee to see their pay history.\n"
                        + "• Click a row to see the full breakdown — taxes, health, retirement "
                        + "contributions, employer match, and the final net pay — exactly like a "
                        + "printed paystub.\n"
                        + "• If the list is empty, no payroll has been saved for that employee yet — "
                        + "go to Payroll Calculator and use SAVE PAYSLIP.",
                "Payslips নির্বাচিত কর্মচারীর জন্য যত পেরোল আসলেই সংরক্ষিত হয়েছে (শুধু প্রিভিউ নয়) তা দেখায়।\n\n"
                        + "• পে-হিস্ট্রি দেখতে একজন কর্মচারী বেছে নিন।\n"
                        + "• সারিতে ক্লিক করলে সম্পূর্ণ বিস্তারিত দেখা যাবে — ট্যাক্স, হেলথ, রিটায়ারমেন্ট "
                        + "কন্ট্রিবিউশন, এমপ্লয়ার ম্যাচ এবং চূড়ান্ত নেট পে — একদম ছাপানো পে-স্টাবের মতো।\n"
                        + "• তালিকা খালি থাকলে বুঝবেন সেই কর্মচারীর কোনো পেরোল এখনো সংরক্ষিত হয়নি — "
                        + "Payroll Calculator-এ গিয়ে SAVE PAYSLIP ব্যবহার করুন।",
                "Recibos de Pago muestra todas las nóminas que realmente se han guardado (no solo "
                        + "previsualizado) para el empleado seleccionado.\n\n"
                        + "• Seleccione un empleado para ver su historial de pagos.\n"
                        + "• Haga clic en una fila para ver el desglose completo — impuestos, salud, "
                        + "aportes de jubilación, aporte del empleador y el pago neto final — igual que "
                        + "un talón de pago impreso.\n"
                        + "• Si la lista está vacía, no se ha guardado ninguna nómina para ese empleado "
                        + "— vaya a la Calculadora de Nómina y use GUARDAR RECIBO.",
                "Payslips चुने गए कर्मचारी के लिए वह हर पेरोल दिखाता है जो वास्तव में सेव हुआ है (सिर्फ "
                        + "प्रीव्यू नहीं)।\n\n"
                        + "• पे-हिस्ट्री देखने के लिए एक कर्मचारी चुनें।\n"
                        + "• पूरी जानकारी देखने के लिए किसी पंक्ति पर क्लिक करें — टैक्स, हेल्थ, रिटायरमेंट "
                        + "कंट्रीब्यूशन, एम्प्लॉयर मैच और अंतिम नेट पे — बिल्कुल प्रिंटेड पेस्टब की तरह।\n"
                        + "• यदि सूची खाली है, तो उस कर्मचारी के लिए अभी तक कोई पेरोल सेव नहीं हुआ है — "
                        + "Payroll Calculator में जाएं और SAVE PAYSLIP का उपयोग करें।",
                "Fiches de Paie affiche chaque paie qui a réellement été enregistrée (pas seulement "
                        + "prévisualisée) pour l'employé sélectionné.\n\n"
                        + "• Sélectionnez un employé pour voir son historique de paie.\n"
                        + "• Cliquez sur une ligne pour voir le détail complet — impôts, santé, "
                        + "cotisations de retraite, contribution de l'employeur et le salaire net final "
                        + "— exactement comme une fiche de paie imprimée.\n"
                        + "• Si la liste est vide, aucune paie n'a encore été enregistrée pour cet "
                        + "employé — allez dans le Calculateur de Paie et utilisez ENREGISTRER LA FICHE.",
                "工资单会显示所选员工所有已实际保存（而非仅预览）的薪资记录。\n\n"
                        + "• 选择一名员工以查看其薪资历史。\n"
                        + "• 点击某一行可查看完整明细 — 税款、医疗、退休金缴款、雇主匹配以及最终净工资 — 与打印出来的工资单完全一致。\n"
                        + "• 如果列表为空，说明该员工还没有已保存的薪资记录 — 请前往薪资计算器并使用“保存工资单”。");

        put("help.reports.body",
                "Reports gives a quick snapshot of the whole company: total headcount, how many "
                        + "employees are active vs. inactive, the average hourly rate, and a "
                        + "breakdown of headcount by department. It refreshes from the Employees "
                        + "list every time you open it.",
                "Reports সম্পূর্ণ কোম্পানির একটি দ্রুত সারসংক্ষেপ দেখায়: মোট কর্মচারী সংখ্যা, কতজন সক্রিয় "
                        + "বা নিষ্ক্রিয়, গড় ঘণ্টাপ্রতি রেট, এবং বিভাগ অনুযায়ী কর্মচারী সংখ্যা। এটি প্রতিবার "
                        + "খোলার সময় Employees তালিকা থেকে রিফ্রেশ হয়।",
                "Informes ofrece una vista rápida de toda la empresa: personal total, cuántos "
                        + "empleados están activos frente a inactivos, la tarifa horaria promedio y un "
                        + "desglose del personal por departamento. Se actualiza a partir de la lista de "
                        + "Empleados cada vez que lo abre.",
                "Reports पूरी कंपनी का एक त्वरित सारांश देता है: कुल कर्मचारी संख्या, कितने कर्मचारी सक्रिय "
                        + "बनाम निष्क्रिय हैं, औसत प्रति घंटा दर, और विभाग अनुसार कर्मचारी संख्या का विवरण। "
                        + "यह हर बार खोलने पर Employees सूची से रीफ्रेश होता है।",
                "Rapports donne un aperçu rapide de toute l'entreprise : effectif total, nombre "
                        + "d'employés actifs contre inactifs, taux horaire moyen, et une répartition de "
                        + "l'effectif par département. Il s'actualise à partir de la liste des Employés "
                        + "chaque fois que vous l'ouvrez.",
                "“报表”提供整个公司的快速概览：员工总数、在职与非在职员工人数、平均时薪，以及各部门的人数分布。每次打开时都会根据“员工”列表自动刷新。");

        put("help.settings.body",
                "EPMS supports 6 display languages: English, বাংলা (Bangla), Español, हिन्दी, "
                        + "Français, and 中文 (Chinese). Open Settings from the main menu, choose a "
                        + "language, and click APPLY LANGUAGE.\n\n"
                        + "The change applies immediately to the menu, the Dashboard, this Help "
                        + "Center, Login, and the Time Clock, Payslips, Reports, Payroll Accounting, "
                        + "W-2 Forms and Settings screens, without restarting the app. Your choice "
                        + "lasts for the rest of this session; the Employees and Payroll Calculator "
                        + "data-entry forms keep their original English labels for now.\n\n"
                        + "More languages can be added later the same way — by adding another column "
                        + "of translations next to the existing ones in the application's language file.",
                "EPMS ৬টি ডিসপ্লে ভাষা সমর্থন করে: English, বাংলা, Español, हिन्दी, Français এবং 中文 "
                        + "(Chinese)। মূল মেনু থেকে Settings খুলুন, একটি ভাষা বেছে নিন, তারপর APPLY LANGUAGE চাপুন।\n\n"
                        + "পরিবর্তনটি সাথে সাথে মেনু, ড্যাশবোর্ড, এই সাহায্য কেন্দ্র, Login এবং Time Clock, "
                        + "Payslips, Reports, Payroll Accounting, W-2 Forms ও Settings স্ক্রিনে প্রয়োগ হয়ে "
                        + "যাবে, অ্যাপ পুনরায় চালু করার প্রয়োজন নেই। আপনার পছন্দ এই সেশন চলা পর্যন্ত থাকবে; "
                        + "Employees এবং Payroll Calculator ফর্ম এখনো মূল (ইংরেজি) লেবেলেই থাকবে।\n\n"
                        + "ভবিষ্যতে আরও ভাষা একইভাবে যুক্ত করা সম্ভব — অ্যাপ্লিকেশনের ভাষার ফাইলে বিদ্যমান "
                        + "কলামগুলোর পাশে আরেকটি অনুবাদ কলাম যুক্ত করে।",
                "EPMS admite 6 idiomas de visualización: English, বাংলা (Bangla), Español, हिन्दी, "
                        + "Français y 中文 (Chinese). Abra Configuración desde el menú principal, elija "
                        + "un idioma y haga clic en APLICAR IDIOMA.\n\n"
                        + "El cambio se aplica de inmediato al menú, al Panel, a este Centro de Ayuda, "
                        + "al inicio de sesión, y a las pantallas de Reloj de Trabajo, Recibos de Pago, "
                        + "Informes, Contabilidad de Nómina, Formularios W-2 y Configuración, sin "
                        + "reiniciar la aplicación. Su elección dura el resto de esta sesión; los "
                        + "formularios de Empleados y Calculadora de Nómina mantienen por ahora sus "
                        + "etiquetas originales en inglés.\n\n"
                        + "Más idiomas se pueden agregar más adelante de la misma manera — añadiendo "
                        + "otra columna de traducciones junto a las existentes en el archivo de idioma "
                        + "de la aplicación.",
                "EPMS 6 डिस्प्ले भाषाओं का समर्थन करता है: English, বাংলা (Bangla), Español, हिन्दी, "
                        + "Français और 中文 (Chinese)। मुख्य मेनू से Settings खोलें, एक भाषा चुनें, फिर "
                        + "APPLY LANGUAGE पर क्लिक करें।\n\n"
                        + "यह परिवर्तन तुरंत मेनू, डैशबोर्ड, इस सहायता केंद्र, Login और Time Clock, Payslips, "
                        + "Reports, Payroll Accounting, W-2 Forms व Settings स्क्रीन पर लागू हो जाता है, "
                        + "ऐप को फिर से शुरू करने की आवश्यकता नहीं है। आपकी पसंद इस सेशन के बाकी हिस्से के "
                        + "लिए बनी रहती है; Employees और Payroll Calculator फॉर्म अभी भी अपने मूल अंग्रेज़ी "
                        + "लेबल में रहते हैं।\n\n"
                        + "बाद में इसी तरह और भाषाएं जोड़ी जा सकती हैं — एप्लिकेशन की भाषा फ़ाइल में मौजूदा "
                        + "कॉलम के बगल में एक और अनुवाद कॉलम जोड़कर।",
                "EPMS prend en charge 6 langues d'affichage : English, বাংলা (Bangla), Español, "
                        + "हिन्दी, Français et 中文 (Chinese). Ouvrez Paramètres depuis le menu "
                        + "principal, choisissez une langue, puis cliquez sur APPLIQUER LA LANGUE.\n\n"
                        + "Le changement s'applique immédiatement au menu, au Tableau de bord, à ce "
                        + "Centre d'Aide, à la connexion, ainsi qu'aux écrans Pointeuse, Fiches de Paie, "
                        + "Rapports, Comptabilité de Paie, Formulaires W-2 et Paramètres, sans "
                        + "redémarrer l'application. Votre choix dure le reste de cette session ; les "
                        + "formulaires Employés et Calculateur de Paie conservent pour l'instant leurs "
                        + "libellés originaux en anglais.\n\n"
                        + "D'autres langues pourront être ajoutées plus tard de la même manière — en "
                        + "ajoutant une autre colonne de traductions à côté des colonnes existantes "
                        + "dans le fichier de langue de l'application.",
                "EPMS 支持 6 种界面语言：English、বাংলা (孟加拉语)、Español、हिन्दी、Français 和 中文。请从主"
                        + "菜单打开“设置”，选择一种语言，然后点击“应用语言”。\n\n"
                        + "更改会立即应用于菜单、仪表盘、此帮助中心、登录界面，以及打卡钟、工资单、报表、薪资会计、"
                        + "W-2 表格和设置界面，无需重启应用程序。您的选择会在本次会话中一直保留；“员工”和“薪资计算"
                        + "器”表单目前仍保留原始英文标签。\n\n"
                        + "以后可以用同样的方式添加更多语言 — 只需在应用程序的语言文件中，在现有列旁边再添加一列翻译。");

        // ---- Payroll Accounting ----
        put("menu.accounting", "Payroll Accounting", "পেরোল অ্যাকাউন্টিং", "Contabilidad de Nómina", "पेरोल अकाउंटिंग",
                "Comptabilité de Paie", "薪资会计");
        put("accounting.title", "Payroll Accounting", "পেরোল অ্যাকাউন্টিং", "Contabilidad de Nómina", "पेरोल अकाउंटिंग",
                "Comptabilité de Paie", "薪资会计");
        put("accounting.select", "Filter by Employee", "কর্মচারী দিয়ে ফিল্টার করুন", "Filtrar por Empleado",
                "कर्मचारी द्वारा फ़िल्टर करें", "Filtrer par Employé", "按员工筛选");
        put("accounting.all", "ALL ENTRIES", "সব এন্ট্রি", "Todos los asientos", "सभी एंट्री", "Toutes les écritures", "全部记录");
        put("accounting.journal", "General Ledger Journal", "জেনারেল লেজার জার্নাল", "Diario del Libro Mayor",
                "जनरल लेजर जर्नल", "Journal du Grand Livre", "总账日记账");
        put("accounting.balances", "Account Balances", "অ্যাকাউন্ট ব্যালেন্স", "Saldos de Cuentas", "अकाउंट बैलेंस",
                "Soldes des Comptes", "账户余额");
        put("accounting.refresh", "REFRESH BALANCES", "ব্যালেন্স রিফ্রেশ করুন", "Actualizar saldos", "बैलेंस रीफ्रेश करें",
                "Actualiser les soldes", "刷新余额");
        put("accounting.col.date", "Date", "তারিখ", "Fecha", "तारीख", "Date", "日期");
        put("accounting.col.employee", "Employee", "কর্মচারী", "Empleado", "कर्मचारी", "Employé", "员工");
        put("accounting.col.description", "Description", "বিবরণ", "Descripción", "विवरण", "Description", "说明");
        put("accounting.col.gross", "Gross Pay", "গ্রস পে", "Pago Bruto", "ग्रॉस पे", "Salaire Brut", "总工资");
        put("accounting.col.net", "Net Pay", "নেট পে", "Pago Neto", "नेट पे", "Salaire Net", "净工资");
        put("accounting.col.code", "Code", "কোড", "Código", "कोड", "Code", "编码");
        put("accounting.col.name", "Account", "অ্যাকাউন্ট", "Cuenta", "अकाउंट", "Compte", "账户");
        put("accounting.col.type", "Type", "ধরন", "Tipo", "प्रकार", "Type", "类型");
        put("accounting.col.debit", "Debit", "ডেবিট", "Débito", "डेबिट", "Débit", "借方");
        put("accounting.col.credit", "Credit", "ক্রেডিট", "Crédito", "क्रेडिट", "Crédit", "贷方");
        put("accounting.col.balance", "Balance", "ব্যালেন্স", "Saldo", "बैलेंस", "Solde", "余额");
        put("accounting.detail.title", "Journal Lines", "জার্নাল লাইন", "Líneas del Asiento", "जर्नल लाइन",
                "Lignes de l'Écriture", "分录明细");
        put("accounting.ytd.title", "YTD Tax Summary", "চলতি বছরের ট্যাক্স সারসংক্ষেপ (YTD)",
                "Resumen de Impuestos Anual (YTD)", "वर्ष-दर-वर्ष (YTD) टैक्स सारांश",
                "Résumé des Taxes Cumulées (YTD)", "年度累计（YTD）税务汇总");
        put("accounting.ytd.sswages", "YTD Social Security Wages", "চলতি বছরের সোশ্যাল সিকিউরিটি ওয়েজ",
                "Salario Sujeto a Seguro Social (YTD)", "YTD सोशल सिक्योरिटी वेतन",
                "Salaires Sécurité Sociale Cumulés (YTD)", "年度累计社保工资");
        put("accounting.ytd.ssroom", "Remaining SS Wage-Base Room", "বাকি SS ওয়েজ-বেস সীমা",
                "Margen Restante de la Base Salarial del SS", "बची हुई SS वेज-बेस सीमा",
                "Marge Restante sur la Base Salariale SS", "社保工资基数剩余额度");
        put("accounting.ytd.medwages", "YTD Medicare Wages", "চলতি বছরের মেডিকেয়ার ওয়েজ",
                "Salario Sujeto a Medicare (YTD)", "YTD मेडिकेयर वेतन", "Salaires Medicare Cumulés (YTD)", "年度累计医保工资");
        put("accounting.ytd.fedwages", "YTD Federal Taxable Wages", "চলতি বছরের ফেডারেল ট্যাক্সেবল ওয়েজ",
                "Salario Federal Imponible (YTD)", "YTD फेडरल टैक्सेबल वेतन",
                "Salaires Fédéraux Imposables Cumulés (YTD)", "年度累计联邦应税工资");
        put("accounting.ytd.gross", "YTD Gross Pay", "চলতি বছরের গ্রস পে", "Pago Bruto Anual (YTD)", "YTD ग्रॉस पे",
                "Salaire Brut Cumulé (YTD)", "年度累计总工资");
        put("accounting.ytd.net", "YTD Net Pay", "চলতি বছরের নেট পে", "Pago Neto Anual (YTD)", "YTD नेट पे",
                "Salaire Net Cumulé (YTD)", "年度累计净工资");

        // ---- W-2 ----
        put("menu.w2", "W-2 Forms", "W-2 ফর্ম", "Formularios W-2", "W-2 फॉर्म", "Formulaires W-2", "W-2 表格");
        put("w2.title", "W-2 Forms", "W-2 ফর্ম", "Formularios W-2", "W-2 फॉर्म", "Formulaires W-2", "W-2 表格");
        put("w2.select", "Select Employee", "কর্মচারী বেছে নিন", "Seleccionar Empleado", "कर्मचारी चुनें",
                "Sélectionner un Employé", "选择员工");
        put("w2.year", "Tax Year", "ট্যাক্স ইয়ার", "Año Fiscal", "टैक्स ईयर", "Année Fiscale", "税务年度");
        put("w2.generate", "GENERATE W-2", "W-2 তৈরি করুন", "Generar W-2", "W-2 जनरेट करें", "Générer le W-2", "生成 W-2");
        put("w2.allyear", "ALL EMPLOYEES FOR YEAR", "এই বছরের সব কর্মচারী", "Todos los empleados del año",
                "इस वर्ष के सभी कर्मचारी", "Tous les employés de l'année", "该年度全部员工");
        put("w2.detail.title", "W-2 Wage and Tax Statement", "W-2 ওয়েজ অ্যান্ড ট্যাক্স স্টেটমেন্ট",
                "W-2 Declaración de Salarios e Impuestos", "W-2 वेज एंड टैक्स स्टेटमेंट",
                "W-2 Relevé de Salaires et d'Impôts", "W-2 工资与税务报表");
        put("w2.box1", "Box 1 — Wages, tips, other compensation", "বক্স ১ — মজুরি, টিপস, অন্যান্য ক্ষতিপূরণ",
                "Casilla 1 — Salarios, propinas y otras compensaciones", "बॉक्स १ — वेतन, टिप्स, अन्य मुआवज़ा",
                "Case 1 — Salaires, pourboires et autres rémunérations", "第1栏 — 工资、小费及其他报酬");
        put("w2.box2", "Box 2 — Federal income tax withheld", "বক্স ২ — ফেডারেল ইনকাম ট্যাক্স কর্তন",
                "Casilla 2 — Impuesto federal sobre la renta retenido", "बॉक्स २ — फेडरल इनकम टैक्स कटौती",
                "Case 2 — Impôt fédéral sur le revenu retenu", "第2栏 — 已代扣联邦所得税");
        put("w2.box3", "Box 3 — Social security wages", "বক্স ৩ — সোশ্যাল সিকিউরিটি ওয়েজ",
                "Casilla 3 — Salarios del Seguro Social", "बॉक्स ३ — सोशल सिक्योरिटी वेतन",
                "Case 3 — Salaires soumis à la Sécurité Sociale", "第3栏 — 社会保障工资");
        put("w2.box4", "Box 4 — Social security tax withheld", "বক্স ৪ — সোশ্যাল সিকিউরিটি ট্যাক্স কর্তন",
                "Casilla 4 — Impuesto del Seguro Social retenido", "बॉक्स ४ — सोशल सिक्योरिटी टैक्स कटौती",
                "Case 4 — Impôt de Sécurité Sociale retenu", "第4栏 — 已代扣社会保障税");
        put("w2.box5", "Box 5 — Medicare wages and tips", "বক্স ৫ — মেডিকেয়ার ওয়েজ ও টিপস",
                "Casilla 5 — Salarios y propinas de Medicare", "बॉक्स ५ — मेडिकेयर वेतन व टिप्स",
                "Case 5 — Salaires et pourboires Medicare", "第5栏 — 医疗保险工资及小费");
        put("w2.box6", "Box 6 — Medicare tax withheld", "বক্স ৬ — মেডিকেয়ার ট্যাক্স কর্তন",
                "Casilla 6 — Impuesto de Medicare retenido", "बॉक्स ६ — मेडिकेयर टैक्स कटौती",
                "Case 6 — Impôt Medicare retenu", "第6栏 — 已代扣医疗保险税");
        put("w2.box16", "Box 16 — State wages, tips, etc.", "বক্স ১৬ — স্টেট ওয়েজ, টিপস ইত্যাদি",
                "Casilla 16 — Salarios estatales, propinas, etc.", "बॉक्स १६ — स्टेट वेतन, टिप्स आदि",
                "Case 16 — Salaires d'État, pourboires, etc.", "第16栏 — 州工资、小费等");
        put("w2.box17", "Box 17 — State income tax", "বক্স ১৭ — স্টেট ইনকাম ট্যাক্স",
                "Casilla 17 — Impuesto estatal sobre la renta", "बॉक्स १७ — स्टेट इनकम टैक्स",
                "Case 17 — Impôt d'État sur le revenu", "第17栏 — 州所得税");
        put("w2.box18", "Box 18 — Local wages, tips, etc.", "বক্স ১৮ — লোকাল ওয়েজ, টিপস ইত্যাদি",
                "Casilla 18 — Salarios locales, propinas, etc.", "बॉक्स १८ — लोकल वेतन, टिप्स आदि",
                "Case 18 — Salaires locaux, pourboires, etc.", "第18栏 — 地方工资、小费等");
        put("w2.box19", "Box 19 — Local income tax", "বক্স ১৯ — লোকাল ইনকাম ট্যাক্স",
                "Casilla 19 — Impuesto local sobre la renta", "बॉक्स १९ — लोकल इनकम टैक्स",
                "Case 19 — Impôt local sur le revenu", "第19栏 — 地方所得税");
        put("w2.box12d", "Box 12 D — 401(k) elective deferrals", "বক্স ১২ D — ৪০১(k) ঐচ্ছিক ডিফারেল",
                "Casilla 12 D — Aportes electivos al 401(k)", "बॉक्स १२ D — 401(k) ऐच्छिक डिफरल",
                "Case 12 D — Cotisations facultatives au 401(k)", "第12栏D — 401(k) 自愿递延缴款");
        put("w2.box12aa", "Box 12 AA — Roth 401(k) contributions", "বক্স ১২ AA — রথ ৪০১(k) কন্ট্রিবিউশন",
                "Casilla 12 AA — Aportes al Roth 401(k)", "बॉक्स १२ AA — रॉथ 401(k) कंट्रीब्यूशन",
                "Case 12 AA — Cotisations Roth 401(k)", "第12栏AA — Roth 401(k) 缴款");
        put("w2.col.employee", "Employee", "কর্মচারী", "Empleado", "कर्मचारी", "Employé", "员工");
        put("w2.col.box1", "Box 1", "বক্স ১", "Casilla 1", "बॉक्स १", "Case 1", "第1栏");
        put("w2.col.box2", "Box 2", "বক্স ২", "Casilla 2", "बॉक्स २", "Case 2", "第2栏");
        put("w2.col.box3", "Box 3", "বক্স ৩", "Casilla 3", "बॉक्स ३", "Case 3", "第3栏");
        put("w2.col.box4", "Box 4", "বক্স ৪", "Casilla 4", "बॉक्स ४", "Case 4", "第4栏");
        put("w2.col.box5", "Box 5", "বক্স ৫", "Casilla 5", "बॉक्स ५", "Case 5", "第5栏");
        put("w2.col.box6", "Box 6", "বক্স ৬", "Casilla 6", "बॉक्स ६", "Case 6", "第6栏");
        put("w2.empty", "No payroll saved yet for this employee/year — nothing to report on a W-2.",
                "এই কর্মচারী/বছরের জন্য এখনো কোনো পেরোল সংরক্ষিত নেই — W-2-এ রিপোর্ট করার কিছু নেই।",
                "Aún no hay nómina guardada para este empleado/año — no hay nada que informar en un W-2.",
                "इस कर्मचारी/वर्ष के लिए अभी तक कोई पेरोल सेव नहीं है — W-2 पर रिपोर्ट करने के लिए कुछ नहीं है।",
                "Aucune paie enregistrée pour cet employé/cette année — rien à déclarer sur un W-2.",
                "该员工/年度尚未保存任何薪资记录 — W-2 上没有可报告的内容。");

        // ---- Help: Accounting & W-2 ----
        put("help.topic.accounting", "Payroll Accounting", "পেরোল অ্যাকাউন্টিং", "Contabilidad de Nómina",
                "पेरोल अकाउंटिंग", "Comptabilité de Paie", "薪资会计");
        put("help.topic.w2", "W-2 Forms", "W-2 ফর্ম", "Formularios W-2", "W-2 फॉर्म", "Formulaires W-2", "W-2 表格");
        put("help.accounting.body",
                "Payroll Accounting turns every saved payroll into a balanced double-entry "
                        + "journal entry automatically — you do not create it by hand.\n\n"
                        + "Each entry debits Salaries & Wages Expense, the employer's FICA match, and "
                        + "the 401(k) employer match; it credits the Federal, FICA, State, Local, LST "
                        + "and SUI payable accounts, the Retirement and Health Insurance payables, and "
                        + "Net Pay Payable — so debits always equal credits.\n\n"
                        + "• Filter the journal by employee, or view ALL ENTRIES.\n"
                        + "• Click a journal row to see its individual debit/credit lines.\n"
                        + "• REFRESH BALANCES recomputes the running balance of every account in the "
                        + "chart of accounts from all posted journal entries.\n"
                        + "• The YTD Tax Summary shows how close an employee is to the annual Social "
                        + "Security wage base — once it is reached, Social Security tax stops being "
                        + "withheld for the rest of that calendar year, exactly like real payroll law.",
                "Payroll Accounting প্রতিটি সংরক্ষিত পেরোলকে স্বয়ংক্রিয়ভাবে একটি ব্যালেন্সড ডাবল-এন্ট্রি "
                        + "জার্নাল এন্ট্রিতে রূপান্তর করে — হাতে তৈরি করতে হয় না।\n\n"
                        + "প্রতিটি এন্ট্রি ডেবিট করে Salaries & Wages Expense, এমপ্লয়ারের FICA ম্যাচ এবং "
                        + "৪০১(k) এমপ্লয়ার ম্যাচ; এবং ক্রেডিট করে ফেডারেল, FICA, স্টেট, লোকাল, LST ও SUI "
                        + "পেয়েবল অ্যাকাউন্ট, রিটায়ারমেন্ট ও হেলথ ইনসিওরেন্স পেয়েবল, এবং নেট পে পেয়েবল — "
                        + "ফলে ডেবিট সবসময় ক্রেডিটের সমান থাকে।\n\n"
                        + "• কর্মচারী দিয়ে জার্নাল ফিল্টার করুন, অথবা ALL ENTRIES দেখুন।\n"
                        + "• কোনো জার্নাল সারিতে ক্লিক করলে তার ডেবিট/ক্রেডিট লাইনগুলো দেখা যাবে।\n"
                        + "• REFRESH BALANCES সব পোস্ট করা জার্নাল থেকে প্রতিটি অ্যাকাউন্টের ব্যালেন্স নতুন করে হিসাব করে।\n"
                        + "• YTD Tax Summary দেখায় একজন কর্মচারী বার্ষিক Social Security ওয়েজ-বেসের কতটা "
                        + "কাছে আছে — সীমা পার হলে সেই ক্যালেন্ডার বছরের বাকি সময় সোশ্যাল সিকিউরিটি ট্যাক্স "
                        + "কর্তন বন্ধ হয়ে যায়, ঠিক আসল পেরোল আইনের মতো।",
                "Payroll Accounting convierte automáticamente cada nómina guardada en un asiento "
                        + "contable de doble entrada balanceado — usted no lo crea a mano.\n\n"
                        + "Cada asiento debita Gastos de Salarios y Sueldos, el aporte FICA del "
                        + "empleador y el aporte del empleador al 401(k); y acredita las cuentas por "
                        + "pagar Federal, FICA, Estatal, Local, LST y SUI, las cuentas por pagar de "
                        + "Jubilación y Seguro de Salud, y el Pago Neto por Pagar — así los débitos "
                        + "siempre igualan a los créditos.\n\n"
                        + "• Filtre el diario por empleado, o vea TODOS LOS ASIENTOS.\n"
                        + "• Haga clic en una fila del diario para ver sus líneas de débito/crédito individuales.\n"
                        + "• ACTUALIZAR SALDOS recalcula el saldo corriente de cada cuenta del catálogo "
                        + "a partir de todos los asientos contabilizados.\n"
                        + "• El Resumen de Impuestos Anual (YTD) muestra qué tan cerca está un empleado "
                        + "de la base salarial anual del Seguro Social — una vez alcanzada, el impuesto "
                        + "del Seguro Social deja de retenerse por el resto de ese año calendario, "
                        + "exactamente como en la ley de nómina real.",
                "Payroll Accounting हर सेव किए गए पेरोल को स्वचालित रूप से एक बैलेंस्ड डबल-एंट्री जर्नल "
                        + "एंट्री में बदल देता है — इसे हाथ से बनाने की ज़रूरत नहीं है।\n\n"
                        + "हर एंट्री Salaries & Wages Expense, एम्प्लॉयर के FICA मैच और 401(k) एम्प्लॉयर "
                        + "मैच को डेबिट करती है; और फेडरल, FICA, स्टेट, लोकल, LST व SUI पेएबल अकाउंट, "
                        + "रिटायरमेंट व हेल्थ इंश्योरेंस पेएबल, और नेट पे पेएबल को क्रेडिट करती है — इसलिए "
                        + "डेबिट हमेशा क्रेडिट के बराबर रहता है।\n\n"
                        + "• जर्नल को कर्मचारी अनुसार फ़िल्टर करें, या ALL ENTRIES देखें।\n"
                        + "• किसी जर्नल पंक्ति पर क्लिक करके उसकी डेबिट/क्रेडिट लाइनें देखें।\n"
                        + "• REFRESH BALANCES सभी पोस्ट की गई जर्नल एंट्री से हर अकाउंट का बैलेंस फिर से गणना करता है।\n"
                        + "• YTD Tax Summary दिखाता है कि कोई कर्मचारी वार्षिक Social Security वेज-बेस के "
                        + "कितना करीब है — सीमा पूरी होने पर उस कैलेंडर वर्ष के बाकी हिस्से में सोशल "
                        + "सिक्योरिटी टैक्स कटना बंद हो जाता है, ठीक असली पेरोल कानून की तरह।",
                "Payroll Accounting transforme automatiquement chaque paie enregistrée en une "
                        + "écriture comptable équilibrée en partie double — vous ne la créez pas à la main.\n\n"
                        + "Chaque écriture débite les Charges de Salaires, la contribution FICA de "
                        + "l'employeur et la contribution patronale au 401(k) ; elle crédite les comptes "
                        + "à payer Fédéral, FICA, État, Local, LST et SUI, les comptes à payer de "
                        + "Retraite et d'Assurance Santé, ainsi que le Salaire Net à Payer — les débits "
                        + "égalent donc toujours les crédits.\n\n"
                        + "• Filtrez le journal par employé, ou consultez TOUTES LES ÉCRITURES.\n"
                        + "• Cliquez sur une ligne du journal pour voir ses lignes de débit/crédit individuelles.\n"
                        + "• ACTUALISER LES SOLDES recalcule le solde courant de chaque compte du plan "
                        + "comptable à partir de toutes les écritures comptabilisées.\n"
                        + "• Le Résumé des Taxes Cumulées (YTD) montre à quel point un employé approche "
                        + "de la base salariale annuelle de la Sécurité Sociale — une fois atteinte, "
                        + "l'impôt de Sécurité Sociale cesse d'être retenu pour le reste de cette année "
                        + "civile, exactement comme dans la vraie législation sur la paie.",
                "薪资会计会自动将每一笔已保存的薪资转换为一条平衡的复式记账日记账分录 — 无需手动创建。\n\n"
                        + "每条分录都会借记“薪资费用”、雇主的 FICA 匹配部分以及 401(k) 雇主匹配部分；并贷记联邦、"
                        + "FICA、州、地方、LST 与 SUI 应付账户、退休金及医疗保险应付账户，以及应付净工资 — 因此借"
                        + "方总额始终等于贷方总额。\n\n"
                        + "• 按员工筛选日记账，或查看“全部记录”。\n"
                        + "• 点击某一条日记账记录可查看其借方/贷方明细。\n"
                        + "• “刷新余额”会根据所有已过账的日记账分录，重新计算会计科目表中每个账户的当前余额。\n"
                        + "• 年度累计税务汇总显示某员工距离年度社会保障工资基数还有多少空间 — 一旦达到该基数，"
                        + "该日历年度剩余时间内将不再代扣社会保障税，与真实薪资法规完全一致。");
        put("help.w2.body",
                "W-2 Forms builds an employee's annual Wage and Tax Statement directly from every "
                        + "payroll saved for that employee in the chosen tax year — nothing is entered "
                        + "by hand.\n\n"
                        + "• Pick an employee and a tax year, then click GENERATE W-2 to see the box-by-"
                        + "box breakdown (wages, federal/state/local tax withheld, Social Security and "
                        + "Medicare wages and tax, and 401(k)/Roth contributions in Box 12).\n"
                        + "• Click ALL EMPLOYEES FOR YEAR to see a summary table for everyone who had "
                        + "at least one saved payroll that year.\n"
                        + "• If nothing shows up, no payroll has been saved for that employee in that "
                        + "year yet — go to Payroll Calculator and SAVE PAYSLIP first.",
                "W-2 Forms একজন কর্মচারীর বার্ষিক Wage and Tax Statement সরাসরি সেই কর্মচারীর জন্য "
                        + "নির্বাচিত ট্যাক্স ইয়ারে সংরক্ষিত সব পেরোল থেকে তৈরি করে — হাতে কিছু লিখতে হয় না।\n\n"
                        + "• একজন কর্মচারী ও ট্যাক্স ইয়ার বেছে নিয়ে GENERATE W-2 চাপুন — বক্স-বাই-বক্স "
                        + "বিবরণ দেখা যাবে (ওয়েজ, ফেডারেল/স্টেট/লোকাল ট্যাক্স কর্তন, সোশ্যাল সিকিউরিটি ও "
                        + "মেডিকেয়ার ওয়েজ ও ট্যাক্স, এবং বক্স ১২-এ ৪০১(k)/রথ কন্ট্রিবিউশন)।\n"
                        + "• ALL EMPLOYEES FOR YEAR চাপলে সেই বছরে অন্তত একটি পেরোল সংরক্ষিত হয়েছে এমন "
                        + "সবার একটি সারসংক্ষেপ টেবিল দেখা যাবে।\n"
                        + "• কিছু না দেখালে বুঝবেন সেই কর্মচারীর সেই বছরে এখনো কোনো পেরোল সংরক্ষিত হয়নি — "
                        + "আগে Payroll Calculator-এ গিয়ে SAVE PAYSLIP করুন।",
                "Formularios W-2 genera la Declaración Anual de Salarios e Impuestos de un empleado "
                        + "directamente a partir de cada nómina guardada para ese empleado en el año "
                        + "fiscal elegido — no se ingresa nada a mano.\n\n"
                        + "• Elija un empleado y un año fiscal, luego haga clic en GENERAR W-2 para ver "
                        + "el desglose casilla por casilla (salarios, impuesto federal/estatal/local "
                        + "retenido, salarios e impuesto de Seguro Social y Medicare, y aportes al "
                        + "401(k)/Roth en la Casilla 12).\n"
                        + "• Haga clic en TODOS LOS EMPLEADOS DEL AÑO para ver una tabla resumen de "
                        + "todos los que tuvieron al menos una nómina guardada ese año.\n"
                        + "• Si no aparece nada, aún no se ha guardado ninguna nómina para ese empleado "
                        + "en ese año — vaya primero a la Calculadora de Nómina y use GUARDAR RECIBO.",
                "W-2 Forms किसी कर्मचारी का वार्षिक Wage and Tax Statement सीधे उस कर्मचारी के लिए चुने "
                        + "गए टैक्स ईयर में सेव किए गए हर पेरोल से बनाता है — कुछ भी हाथ से नहीं लिखा जाता।\n\n"
                        + "• एक कर्मचारी और टैक्स ईयर चुनें, फिर बॉक्स-बाय-बॉक्स विवरण देखने के लिए GENERATE "
                        + "W-2 पर क्लिक करें (वेतन, फेडरल/स्टेट/लोकल टैक्स कटौती, सोशल सिक्योरिटी व मेडिकेयर "
                        + "वेतन व टैक्स, और बॉक्स १२ में 401(k)/रॉथ कंट्रीब्यूशन)।\n"
                        + "• ALL EMPLOYEES FOR YEAR पर क्लिक करने से उन सभी लोगों की सारांश टेबल दिखेगी "
                        + "जिनका उस वर्ष कम से कम एक पेरोल सेव हुआ था।\n"
                        + "• कुछ न दिखे तो समझें कि उस कर्मचारी का उस वर्ष अभी तक कोई पेरोल सेव नहीं हुआ है "
                        + "— पहले Payroll Calculator में जाकर SAVE PAYSLIP करें।",
                "Formulaires W-2 génère l'état annuel des salaires et des impôts d'un employé "
                        + "directement à partir de chaque paie enregistrée pour cet employé au cours "
                        + "de l'année fiscale choisie — rien n'est saisi manuellement.\n\n"
                        + "• Choisissez un employé et une année fiscale, puis cliquez sur GÉNÉRER LE "
                        + "W-2 pour voir le détail case par case (salaires, impôt fédéral/d'État/local "
                        + "retenu, salaires et impôt Sécurité Sociale et Medicare, et cotisations "
                        + "401(k)/Roth en Case 12).\n"
                        + "• Cliquez sur TOUS LES EMPLOYÉS DE L'ANNÉE pour voir un tableau récapitulatif "
                        + "de tous ceux qui ont eu au moins une paie enregistrée cette année-là.\n"
                        + "• Si rien ne s'affiche, aucune paie n'a encore été enregistrée pour cet "
                        + "employé cette année-là — allez d'abord dans le Calculateur de Paie et utilisez ENREGISTRER LA FICHE.",
                "W-2 表格会直接根据某员工在所选税务年度内保存的每一笔薪资，生成其年度工资与税务报表 — 无需手动输入"
                        + "任何内容。\n\n"
                        + "• 选择一名员工和一个税务年度，然后点击“生成 W-2”，即可查看逐栏明细（工资、联邦/州/地方"
                        + "已代扣税款、社会保障与医疗保险工资及税款，以及第12栏中的 401(k)/Roth 缴款）。\n"
                        + "• 点击“该年度全部员工”，即可查看当年至少保存过一笔薪资的所有员工的汇总表。\n"
                        + "• 如果没有显示任何内容，说明该员工在该年度还没有保存过薪资 — 请先前往薪资计算器并"
                        + "使用“保存工资单”。");

        // ---- Login ----
        put("login.username", "Username", "ইউজারনেম", "Usuario", "उपयोगकर्ता नाम", "Nom d'utilisateur", "用户名");
        put("login.password", "Password", "পাসওয়ার্ড", "Contraseña", "पासवर्ड", "Mot de passe", "密码");
        put("login.button", "LOG IN", "লগ ইন", "Iniciar sesión", "लॉग इन", "Se connecter", "登录");
        put("login.subtitle", "Payroll Management System", "পেরোল ম্যানেজমেন্ট সিস্টেম", "Sistema de Gestión de Nómina",
                "पेरोल मैनेजमेंट सिस्टम", "Système de Gestion de Paie", "薪资管理系统");
        put("login.error", "Login failed — please check your username and password.",
                "লগইন ব্যর্থ হয়েছে — অনুগ্রহ করে আপনার ইউজারনেম ও পাসওয়ার্ড যাচাই করুন।",
                "Error al iniciar sesión — verifique su usuario y contraseña.",
                "लॉगिन असफल — कृपया अपना उपयोगकर्ता नाम और पासवर्ड जांचें।",
                "Échec de la connexion — veuillez vérifier votre nom d'utilisateur et votre mot de passe.",
                "登录失败 — 请检查您的用户名和密码。");
        put("login.footer", "RbcTcsWorld  •  Enterprise Payroll Management System",
                "RbcTcsWorld  •  এন্টারপ্রাইজ পেরোল ম্যানেজমেন্ট সিস্টেম",
                "RbcTcsWorld  •  Sistema Empresarial de Gestión de Nómina",
                "RbcTcsWorld  •  एंटरप्राइज़ पेरोल मैनेजमेंट सिस्टम",
                "RbcTcsWorld  •  Système de Gestion de Paie d'Entreprise",
                "RbcTcsWorld  •  企业薪资管理系统");

        // ---- Top menu bar (Payroll / Employees / Reports dropdowns) ----
        put("menubar.payroll", "Payroll", "পেরোল", "Nómina", "पेरोल", "Paie", "薪资");
        put("menubar.employees", "Employees", "কর্মচারী", "Empleados", "कर्मचारी", "Employés", "员工");
        put("menubar.reports", "Reports", "রিপোর্ট", "Informes", "रिपोर्ट", "Rapports", "报表");
        put("menubar.payroll.new", "New Payroll", "নতুন পেরোল", "Nueva Nómina", "नया पेरोल", "Nouvelle Paie", "新建薪资");
        put("menubar.payroll.calculate", "Calculate Payroll", "পেরোল হিসাব করুন", "Calcular Nómina", "पेरोल कैलकुलेट करें",
                "Calculer la Paie", "计算薪资");
        put("menubar.payroll.review", "Review Payroll", "পেরোল রিভিউ করুন", "Revisar Nómina", "पेरोल रिव्यू करें",
                "Vérifier la Paie", "审核薪资");
        put("menubar.payroll.approve", "Approve Payroll", "পেরোল অনুমোদন করুন", "Aprobar Nómina", "पेरोल अप्रूव करें",
                "Approuver la Paie", "批准薪资");
        put("menubar.payroll.process", "Process Payroll", "পেরোল প্রসেস করুন", "Procesar Nómina", "पेरोल प्रोसेस करें",
                "Traiter la Paie", "处理薪资");
        put("menubar.payroll.history", "Payroll History", "পেরোল হিস্ট্রি", "Historial de Nómina", "पेरोल हिस्ट्री",
                "Historique de Paie", "薪资历史");
        put("menubar.employees.add", "Add Employee", "কর্মচারী যুক্ত করুন", "Agregar Empleado", "कर्मचारी जोड़ें",
                "Ajouter un Employé", "添加员工");
        put("menubar.employees.list", "Employee List", "কর্মচারী তালিকা", "Lista de Empleados", "कर्मचारी सूची",
                "Liste des Employés", "员工列表");
        put("menubar.employees.search", "Search Employee", "কর্মচারী খুঁজুন", "Buscar Empleado", "कर्मचारी खोजें",
                "Rechercher un Employé", "搜索员工");
        put("menubar.employees.edit", "Edit Employee", "কর্মচারী সম্পাদনা করুন", "Editar Empleado", "कर्मचारी संपादित करें",
                "Modifier un Employé", "编辑员工");
        put("menubar.employees.status", "Employee Status", "কর্মচারীর অবস্থা", "Estado del Empleado", "कर्मचारी स्थिति",
                "Statut de l'Employé", "员工状态");
        put("menubar.employees.history", "Employee History", "কর্মচারীর ইতিহাস", "Historial del Empleado",
                "कर्मचारी इतिहास", "Historique de l'Employé", "员工历史");
        put("menubar.reports.payroll", "Payroll Report", "পেরোল রিপোর্ট", "Informe de Nómina", "पेरोल रिपोर्ट",
                "Rapport de Paie", "薪资报表");
        put("menubar.reports.employee", "Employee Report", "কর্মচারী রিপোর্ট", "Informe de Empleados", "कर्मचारी रिपोर्ट",
                "Rapport des Employés", "员工报表");
        put("menubar.reports.tax", "Tax Report", "ট্যাক্স রিপোর্ট", "Informe de Impuestos", "टैक्स रिपोर्ट",
                "Rapport Fiscal", "税务报表");
        put("menubar.reports.deduction", "Deduction Report", "ডিডাকশন রিপোর্ট", "Informe de Deducciones",
                "डिडक्शन रिपोर्ट", "Rapport des Déductions", "扣款报表");
        put("menubar.reports.hours", "Hours Report", "ঘণ্টার রিপোর্ট", "Informe de Horas", "ऑवर्स रिपोर्ट",
                "Rapport des Heures", "工时报表");
        put("menubar.reports.ytd", "YTD Report", "YTD রিপোর্ট", "Informe Anual (YTD)", "YTD रिपोर्ट",
                "Rapport Cumulé (YTD)", "年度累计报表");
        put("menubar.reports.payslip", "Payslip Report", "পে-স্লিপ রিপোর্ট", "Informe de Recibos", "पेस्लिप रिपोर्ट",
                "Rapport des Fiches de Paie", "工资单报表");
        put("menubar.reports.audit", "Audit Log", "অডিট লগ", "Registro de Auditoría", "ऑडिट लॉग", "Journal d'Audit", "审计日志");
    }
}
