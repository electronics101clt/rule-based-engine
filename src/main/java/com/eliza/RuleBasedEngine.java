package com.eliza;

import java.util.*;

/**
 * Iterative rule-based conversation engine with context awareness
 */
public class RuleBasedEngine {

    private List<Rule> rules;
    private ConversationContext context;
    private Random random;

    public RuleBasedEngine() {
        this.rules = new ArrayList<>();
        this.context = new ConversationContext();
        this.random = new Random();
        initializeRules();
    }

    public String getGreeting() {
        return "Hi! I'm ELIZA. What's on your mind today?";
    }

    public String getResponse(String input) {
        // Find matching rules sorted by priority
        List<Rule> matchingRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.matches(input, context)) {
                matchingRules.add(rule);
            }
        }

        // Sort by priority (higher first)
        matchingRules.sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));

        String response;
        if (!matchingRules.isEmpty()) {
            Rule selectedRule = matchingRules.get(0);
            response = selectedRule.getResponse(context);
            selectedRule.executeActions(context);

            // Update context
            if (selectedRule.getTopic() != null) {
                context.setCurrentTopic(selectedRule.getTopic());
            }
            if (selectedRule.getEmotionalCategory() != null) {
                context.setEmotionalState(selectedRule.getEmotionalCategory());
            }
        } else {
            response = getDefaultResponse();
        }

        // Store in history
        context.addToHistory(input, response);

        return response;
    }

    private String getDefaultResponse() {
        String[] defaults = {
            "Tell me more about that.",
            "How does that make you feel?",
            "Can you elaborate on that?",
            "What does that mean to you?",
            "I see. Please continue.",
            "Interesting. Go on.",
            "What else comes to mind?",
            "How long have you felt this way?",
            "What would you like to discuss?",
            "Can you give me more details?"
        };
        return defaults[random.nextInt(defaults.length)];
    }

    private void initializeRules() {
        initializeEmotionalRules();
        initializeRelationshipRules();
        initializeFamilyRules();
        initializeWorkRules();
        initializeMentalHealthRules();
        initializeCognitiveRules();
        initializeFollowUpRules();
        initializeContextualRules();

        // Extended rule sets for comprehensive coverage
        initializeExtendedEmotionalRules();
        initializeExtendedRelationshipRules();
        initializeExtendedFamilyRules();
        initializeExtendedWorkRules();
        initializeExtendedMentalHealthRules();
    }

    private void initializeEmotionalRules() {
        // DEPRESSION - High priority, multi-turn conversation
        Rule depressionFirst = new Rule("depression-first", 90);
        depressionFirst.addPattern("DEPRESSED");
        depressionFirst.addPattern("DEPRESSION");
        depressionFirst.addResponse("I'm sorry you're feeling depressed. How long have you been experiencing these feelings?");
        depressionFirst.addResponse("Depression can be very difficult. What do you think triggered this?");
        depressionFirst.addResponse("Tell me more about your depression.");
        depressionFirst.setTopic("depression");
        depressionFirst.setEmotionalCategory("negative");
        depressionFirst.addAction(ctx -> ctx.setVariable("discussed_depression", true));
        rules.add(depressionFirst);

        // Follow-up if depression already discussed
        Rule depressionFollowUp = new Rule("depression-followup", 95);
        depressionFollowUp.addPattern("DEPRESSED");
        depressionFollowUp.addPattern("DEPRESSION");
        depressionFollowUp.addCondition(ctx -> ctx.hasVariable("discussed_depression"));
        depressionFollowUp.addResponse("You mentioned depression earlier. Is it getting worse or better?");
        depressionFollowUp.addResponse("Have you been able to identify what makes your depression worse?");
        depressionFollowUp.addResponse("Are you taking any steps to manage your depression?");
        depressionFollowUp.setTopic("depression");
        rules.add(depressionFollowUp);

        // ANXIETY
        Rule anxietyFirst = new Rule("anxiety-first", 90);
        anxietyFirst.addPattern("ANXIOUS");
        anxietyFirst.addPattern("ANXIETY");
        anxietyFirst.addPattern("WORRIED");
        anxietyFirst.addPattern("WORRY");
        anxietyFirst.addResponse("What are you feeling anxious about?");
        anxietyFirst.addResponse("Anxiety can be overwhelming. What triggers these feelings?");
        anxietyFirst.addResponse("Tell me more about your anxiety.");
        anxietyFirst.addResponse("How does the anxiety manifest for you physically?");
        anxietyFirst.addResponse("When did you first notice these anxious feelings?");
        anxietyFirst.setTopic("anxiety");
        anxietyFirst.setEmotionalCategory("negative");
        anxietyFirst.addAction(ctx -> ctx.setVariable("discussed_anxiety", true));
        rules.add(anxietyFirst);

        Rule anxietyFollowUp = new Rule("anxiety-followup", 95);
        anxietyFollowUp.addPattern("ANXIOUS");
        anxietyFollowUp.addPattern("ANXIETY");
        anxietyFollowUp.addCondition(ctx -> ctx.hasVariable("discussed_anxiety"));
        anxietyFollowUp.addResponse("You mentioned anxiety before. Is this related to the same concerns?");
        anxietyFollowUp.addResponse("Have you tried any techniques to manage your anxiety?");
        anxietyFollowUp.addResponse("What helps you feel less anxious?");
        anxietyFollowUp.setTopic("anxiety");
        rules.add(anxietyFollowUp);

        // SADNESS
        Rule sadness = new Rule("sadness", 85);
        sadness.addPattern("SAD");
        sadness.addPattern("SADNESS");
        sadness.addPattern("UNHAPPY");
        sadness.addResponse("What's making you feel sad?");
        sadness.addResponse("Can you tell me more about this sadness?");
        sadness.addResponse("How long have you been feeling this way?");
        sadness.addResponse("What would help you feel less sad?");
        sadness.setTopic("sadness");
        sadness.setEmotionalCategory("negative");
        rules.add(sadness);

        // ANGER
        Rule anger = new Rule("anger", 85);
        anger.addPattern("ANGRY");
        anger.addPattern("ANGER");
        anger.addPattern("MAD");
        anger.addPattern("FURIOUS");
        anger.addPattern("PISSED");
        anger.addResponse("What are you angry about?");
        anger.addResponse("Tell me more about this anger.");
        anger.addResponse("How do you express your anger?");
        anger.addResponse("What triggered these angry feelings?");
        anger.addResponse("Is this anger directed at someone specific?");
        anger.setTopic("anger");
        anger.setEmotionalCategory("negative");
        anger.addAction(ctx -> ctx.setVariable("feeling_angry", true));
        rules.add(anger);

        // HAPPINESS
        Rule happiness = new Rule("happiness", 80);
        happiness.addPattern("HAPPY");
        happiness.addPattern("HAPPINESS");
        happiness.addPattern("JOY");
        happiness.addPattern("JOYFUL");
        happiness.addPattern("GLAD");
        happiness.addResponse("That's wonderful! What's making you happy?");
        happiness.addResponse("I'm glad to hear you're feeling happy. Tell me more!");
        happiness.addResponse("What brought about this happiness?");
        happiness.addResponse("How does this happiness feel different from usual?");
        happiness.setTopic("happiness");
        happiness.setEmotionalCategory("positive");
        rules.add(happiness);

        // FEAR
        Rule fear = new Rule("fear", 85);
        fear.addPattern("AFRAID");
        fear.addPattern("FEAR");
        fear.addPattern("SCARED");
        fear.addPattern("FRIGHTENED");
        fear.addPattern("TERRIFIED");
        fear.addResponse("What are you afraid of?");
        fear.addResponse("Can you describe this fear?");
        fear.addResponse("How does this fear affect your daily life?");
        fear.addResponse("When did you first experience this fear?");
        fear.addResponse("Is this fear based on something specific?");
        fear.setTopic("fear");
        fear.setEmotionalCategory("negative");
        rules.add(fear);

        // STRESS
        Rule stress = new Rule("stress", 85);
        stress.addPattern("STRESSED");
        stress.addPattern("STRESS");
        stress.addPattern("OVERWHELMED");
        stress.addPattern("PRESSURE");
        stress.addResponse("What's causing you stress?");
        stress.addResponse("Tell me about what's overwhelming you.");
        stress.addResponse("How are you coping with this stress?");
        stress.addResponse("What aspects of your life feel most stressful?");
        stress.addResponse("Have you been able to identify your stress triggers?");
        stress.setTopic("stress");
        stress.setEmotionalCategory("negative");
        rules.add(stress);

        // LONELINESS
        Rule lonely = new Rule("lonely", 85);
        lonely.addPattern("LONELY");
        lonely.addPattern("LONELINESS");
        lonely.addPattern("ALONE");
        lonely.addPattern("ISOLATED");
        lonely.addResponse("Feeling lonely can be very painful. Tell me more.");
        lonely.addResponse("What makes you feel lonely?");
        lonely.addResponse("Do you have people you can reach out to?");
        lonely.addResponse("How long have you felt this way?");
        lonely.addResponse("What would help you feel less isolated?");
        lonely.setTopic("loneliness");
        lonely.setEmotionalCategory("negative");
        rules.add(lonely);

        // GUILT
        Rule guilt = new Rule("guilt", 85);
        guilt.addPattern("GUILTY");
        guilt.addPattern("GUILT");
        guilt.addPattern("ASHAMED");
        guilt.addPattern("SHAME");
        guilt.addResponse("What are you feeling guilty about?");
        guilt.addResponse("Can you tell me more about this guilt?");
        guilt.addResponse("Is this guilt justified, do you think?");
        guilt.addResponse("How is this guilt affecting you?");
        guilt.addResponse("Have you talked to anyone about these feelings?");
        guilt.setTopic("guilt");
        guilt.setEmotionalCategory("negative");
        rules.add(guilt);

        // HOPELESSNESS
        Rule hopeless = new Rule("hopeless", 90);
        hopeless.addPattern("HOPELESS");
        hopeless.addPattern("HOPELESSNESS");
        hopeless.addPattern("HELPLESS");
        hopeless.addPattern("WORTHLESS");
        hopeless.addResponse("These feelings of hopelessness concern me. Have you thought about hurting yourself?");
        hopeless.addResponse("What makes you feel hopeless?");
        hopeless.addResponse("Can you think of anything that might help?");
        hopeless.addResponse("Have you felt this way before?");
        hopeless.addResponse("Is there anyone you can talk to about this?");
        hopeless.setTopic("hopelessness");
        hopeless.setEmotionalCategory("crisis");
        rules.add(hopeless);
    }

    private void initializeRelationshipRules() {
        // RELATIONSHIP - General
        Rule relationship = new Rule("relationship", 80);
        relationship.addPattern("RELATIONSHIP");
        relationship.addPattern("PARTNER");
        relationship.addPattern("BOYFRIEND");
        relationship.addPattern("GIRLFRIEND");
        relationship.addPattern("SPOUSE");
        relationship.addResponse("Tell me about your relationship.");
        relationship.addResponse("How long have you been in this relationship?");
        relationship.addResponse("What's happening in your relationship?");
        relationship.addResponse("How do you feel about your partner?");
        relationship.addResponse("What would you like to change about your relationship?");
        relationship.setTopic("relationship");
        relationship.addAction(ctx -> ctx.setVariable("has_partner", true));
        rules.add(relationship);

        // BREAKUP
        Rule breakup = new Rule("breakup", 90);
        breakup.addPattern("BREAKUP");
        breakup.addPattern("BROKE UP");
        breakup.addPattern("BROKEN UP");
        breakup.addPattern("SPLIT UP");
        breakup.addResponse("I'm sorry you're going through a breakup. How are you coping?");
        breakup.addResponse("Breakups can be very difficult. How long has it been?");
        breakup.addResponse("What led to the breakup?");
        breakup.addResponse("How are you feeling about the breakup?");
        breakup.addResponse("Do you have support during this time?");
        breakup.setTopic("breakup");
        breakup.setEmotionalCategory("negative");
        rules.add(breakup);

        // LOVE
        Rule love = new Rule("love", 75);
        love.addPattern("LOVE");
        love.addPattern("IN LOVE");
        love.addPattern("LOVING");
        love.addResponse("Tell me about this love.");
        love.addResponse("How does being in love make you feel?");
        love.addResponse("What does love mean to you?");
        love.addResponse("How do you express your love?");
        love.addResponse("Is this love reciprocated?");
        love.setTopic("love");
        love.setEmotionalCategory("positive");
        rules.add(love);

        // MARRIAGE
        Rule marriage = new Rule("marriage", 80);
        marriage.addPattern("MARRIAGE");
        marriage.addPattern("MARRIED");
        marriage.addPattern("HUSBAND");
        marriage.addPattern("WIFE");
        marriage.addResponse("Tell me about your marriage.");
        marriage.addResponse("How long have you been married?");
        marriage.addResponse("What's your marriage like?");
        marriage.addResponse("How do you and your spouse communicate?");
        marriage.addResponse("What challenges are you facing in your marriage?");
        marriage.setTopic("marriage");
        rules.add(marriage);

        // DIVORCE
        Rule divorce = new Rule("divorce", 90);
        divorce.addPattern("DIVORCE");
        divorce.addPattern("DIVORCED");
        divorce.addPattern("DIVORCING");
        divorce.addResponse("I'm sorry you're going through a divorce. How are you handling it?");
        divorce.addResponse("Divorce can be very stressful. What led to this decision?");
        divorce.addResponse("How long have you been divorced/divorcing?");
        divorce.addResponse("Do you have children involved in the divorce?");
        divorce.addResponse("How are you taking care of yourself during this time?");
        divorce.setTopic("divorce");
        divorce.setEmotionalCategory("negative");
        rules.add(divorce);

        // TRUST
        Rule trust = new Rule("trust", 80);
        trust.addPattern("TRUST");
        trust.addPattern("TRUSTED");
        trust.addPattern("TRUSTING");
        trust.addResponse("What's happening with trust?");
        trust.addResponse("Do you feel you can trust your partner?");
        trust.addResponse("Has trust been broken?");
        trust.addResponse("How important is trust to you in a relationship?");
        trust.addResponse("What would it take to rebuild trust?");
        trust.setTopic("trust");
        rules.add(trust);

        // BETRAYAL
        Rule betrayal = new Rule("betrayal", 90);
        betrayal.addPattern("BETRAYED");
        betrayal.addPattern("BETRAYAL");
        betrayal.addPattern("CHEATING");
        betrayal.addPattern("CHEATED");
        betrayal.addPattern("AFFAIR");
        betrayal.addResponse("I'm sorry you've experienced betrayal. What happened?");
        betrayal.addResponse("Betrayal is very painful. How are you dealing with it?");
        betrayal.addResponse("Who betrayed you?");
        betrayal.addResponse("How did you discover this betrayal?");
        betrayal.addResponse("What are you planning to do about this situation?");
        betrayal.setTopic("betrayal");
        betrayal.setEmotionalCategory("negative");
        rules.add(betrayal);

        // ARGUMENT/FIGHT
        Rule argument = new Rule("argument", 80);
        argument.addPattern("ARGUMENT");
        argument.addPattern("ARGUE");
        argument.addPattern("FIGHTING");
        argument.addPattern("FIGHT");
        argument.addPattern("CONFLICT");
        argument.addResponse("What are you arguing about?");
        argument.addResponse("Tell me about this conflict.");
        argument.addResponse("How often do these arguments happen?");
        argument.addResponse("How do you typically resolve arguments?");
        argument.addResponse("What triggers these fights?");
        argument.setTopic("conflict");
        rules.add(argument);
    }

    private void initializeFamilyRules() {
        // MOTHER
        Rule mother = new Rule("mother", 85);
        mother.addPattern("MOTHER");
        mother.addPattern("MOM");
        mother.addPattern("MAMA");
        mother.addResponse("Tell me about your mother.");
        mother.addResponse("What's your relationship with your mother like?");
        mother.addResponse("How does your mother make you feel?");
        mother.addResponse("What memories of your mother come to mind?");
        mother.addResponse("How often do you talk to your mother?");
        mother.setTopic("mother");
        mother.addAction(ctx -> ctx.setVariable("mentioned_mother", true));
        rules.add(mother);

        // FATHER
        Rule father = new Rule("father", 85);
        father.addPattern("FATHER");
        father.addPattern("DAD");
        father.addPattern("PAPA");
        father.addResponse("Tell me about your father.");
        father.addResponse("What's your relationship with your father like?");
        father.addResponse("How does your father make you feel?");
        father.addResponse("What role does your father play in your life?");
        father.addResponse("How would you describe your father?");
        father.setTopic("father");
        father.addAction(ctx -> ctx.setVariable("mentioned_father", true));
        rules.add(father);

        // PARENTS (when both mentioned)
        Rule parents = new Rule("parents", 87);
        parents.addPattern("PARENTS");
        parents.addCondition(ctx -> ctx.hasVariable("mentioned_mother") || ctx.hasVariable("mentioned_father"));
        parents.addResponse("You've mentioned both parents. How is your relationship with each of them different?");
        parents.addResponse("What role do your parents play in your current life?");
        parents.addResponse("How has your relationship with your parents evolved?");
        parents.setTopic("family");
        rules.add(parents);

        // SIBLING
        Rule sibling = new Rule("sibling", 80);
        sibling.addPattern("BROTHER");
        sibling.addPattern("SISTER");
        sibling.addPattern("SIBLING");
        sibling.addResponse("Tell me about your sibling.");
        sibling.addResponse("What's your relationship like with your sibling?");
        sibling.addResponse("How does your sibling make you feel?");
        sibling.addResponse("Are you close with your sibling?");
        sibling.addResponse("Do you have conflicts with your sibling?");
        sibling.setTopic("sibling");
        rules.add(sibling);

        // FAMILY (general)
        Rule family = new Rule("family", 80);
        family.addPattern("FAMILY");
        family.addResponse("Tell me about your family.");
        family.addResponse("What's your family like?");
        family.addResponse("How important is family to you?");
        family.addResponse("What role does family play in your life?");
        family.addResponse("How do you feel about your family?");
        family.setTopic("family");
        rules.add(family);

        // CHILD/CHILDREN
        Rule child = new Rule("child", 85);
        child.addPattern("CHILD");
        child.addPattern("CHILDREN");
        child.addPattern("SON");
        child.addPattern("DAUGHTER");
        child.addPattern("KIDS");
        child.addResponse("Tell me about your children.");
        child.addResponse("How many children do you have?");
        child.addResponse("What are your children like?");
        child.addResponse("How is your relationship with your children?");
        child.addResponse("What concerns do you have about your children?");
        child.setTopic("children");
        child.addAction(ctx -> ctx.setVariable("has_children", true));
        rules.add(child);
    }

    private void initializeWorkRules() {
        // JOB/WORK
        Rule work = new Rule("work", 75);
        work.addPattern("JOB");
        work.addPattern("WORK");
        work.addPattern("CAREER");
        work.addPattern("EMPLOYMENT");
        work.addResponse("Tell me about your work.");
        work.addResponse("What do you do for work?");
        work.addResponse("How do you feel about your job?");
        work.addResponse("What's happening at work?");
        work.addResponse("Are you satisfied with your career?");
        work.setTopic("work");
        work.addAction(ctx -> ctx.setVariable("discussed_work", true));
        rules.add(work);

        // BOSS
        Rule boss = new Rule("boss", 80);
        boss.addPattern("BOSS");
        boss.addPattern("MANAGER");
        boss.addPattern("SUPERVISOR");
        boss.addResponse("Tell me about your boss.");
        boss.addResponse("What's your relationship with your boss like?");
        boss.addResponse("How does your boss treat you?");
        boss.addResponse("What issues are you having with your boss?");
        boss.addResponse("How does your boss affect your work experience?");
        boss.setTopic("work");
        rules.add(boss);

        // FIRED/QUIT
        Rule jobLoss = new Rule("job-loss", 90);
        jobLoss.addPattern("FIRED");
        jobLoss.addPattern("QUIT");
        jobLoss.addPattern("RESIGNED");
        jobLoss.addPattern("LAID OFF");
        jobLoss.addPattern("UNEMPLOYED");
        jobLoss.addResponse("I'm sorry to hear that. What happened?");
        jobLoss.addResponse("How are you coping with this job loss?");
        jobLoss.addResponse("What led to this situation?");
        jobLoss.addResponse("What are your plans now?");
        jobLoss.addResponse("How is this affecting you financially and emotionally?");
        jobLoss.setTopic("unemployment");
        jobLoss.setEmotionalCategory("negative");
        rules.add(jobLoss);

        // BURNOUT
        Rule burnout = new Rule("burnout", 85);
        burnout.addPattern("BURNOUT");
        burnout.addPattern("BURNED OUT");
        burnout.addPattern("EXHAUSTED");
        burnout.addPattern("OVERWORKED");
        burnout.addResponse("Burnout is serious. How long have you been feeling this way?");
        burnout.addResponse("What's contributing to your burnout?");
        burnout.addResponse("Have you been able to take any time off?");
        burnout.addResponse("What would help you feel less burned out?");
        burnout.addResponse("Are you able to set boundaries at work?");
        burnout.setTopic("burnout");
        burnout.setEmotionalCategory("negative");
        rules.add(burnout);
    }

    private void initializeMentalHealthRules() {
        // THERAPY/THERAPIST
        Rule therapy = new Rule("therapy", 80);
        therapy.addPattern("THERAPY");
        therapy.addPattern("THERAPIST");
        therapy.addPattern("COUNSELING");
        therapy.addPattern("COUNSELOR");
        therapy.addResponse("Are you currently in therapy?");
        therapy.addResponse("How is therapy going for you?");
        therapy.addResponse("What made you seek therapy?");
        therapy.addResponse("How do you feel about therapy?");
        therapy.addResponse("Have you found therapy helpful?");
        therapy.setTopic("therapy");
        rules.add(therapy);

        // MEDICATION
        Rule medication = new Rule("medication", 85);
        medication.addPattern("MEDICATION");
        medication.addPattern("PILLS");
        medication.addPattern("MEDICINE");
        medication.addPattern("ANTIDEPRESSANT");
        medication.addResponse("Are you taking medication?");
        medication.addResponse("How is the medication working for you?");
        medication.addResponse("Are you experiencing any side effects?");
        medication.addResponse("How long have you been on medication?");
        medication.addResponse("Do you have concerns about your medication?");
        medication.setTopic("medication");
        rules.add(medication);

        // SUICIDE (CRISIS - highest priority)
        Rule suicide = new Rule("suicide", 100);
        suicide.addPattern("SUICIDE");
        suicide.addPattern("SUICIDAL");
        suicide.addPattern("KILL MYSELF");
        suicide.addPattern("END IT ALL");
        suicide.addPattern("WANT TO DIE");
        suicide.addResponse("I'm very concerned about you. Please call the National Suicide Prevention Lifeline at 988 immediately.");
        suicide.addResponse("Your life is valuable. Please reach out for help: call 988 or text 'HELLO' to 741741.");
        suicide.addResponse("Please don't hurt yourself. Call 988 right now to talk to someone who can help.");
        suicide.setTopic("crisis");
        suicide.setEmotionalCategory("crisis");
        rules.add(suicide);

        // PANIC ATTACKS
        Rule panic = new Rule("panic", 90);
        panic.addPattern("PANIC");
        panic.addPattern("PANIC ATTACK");
        panic.addPattern("PANICKING");
        panic.addResponse("Panic attacks can be terrifying. When did this start?");
        panic.addResponse("What triggers your panic attacks?");
        panic.addResponse("Have you learned any techniques to manage panic attacks?");
        panic.addResponse("How often are you experiencing panic attacks?");
        panic.addResponse("Are you able to identify when a panic attack is coming?");
        panic.setTopic("panic");
        panic.setEmotionalCategory("negative");
        rules.add(panic);

        // TRAUMA/PTSD
        Rule trauma = new Rule("trauma", 95);
        trauma.addPattern("TRAUMA");
        trauma.addPattern("PTSD");
        trauma.addPattern("TRAUMATIC");
        trauma.addPattern("FLASHBACK");
        trauma.addResponse("I'm sorry you've experienced trauma. Are you getting professional help?");
        trauma.addResponse("Trauma can have lasting effects. How is it affecting you now?");
        trauma.addResponse("What kind of support do you have for dealing with trauma?");
        trauma.addResponse("Have you been able to talk about the trauma with anyone?");
        trauma.addResponse("How long ago did the trauma occur?");
        trauma.setTopic("trauma");
        trauma.setEmotionalCategory("negative");
        rules.add(trauma);

        // ADDICTION
        Rule addiction = new Rule("addiction", 90);
        addiction.addPattern("ADDICTION");
        addiction.addPattern("ADDICTED");
        addiction.addPattern("ALCOHOLIC");
        addiction.addPattern("DRINKING");
        addiction.addPattern("DRUGS");
        addiction.addPattern("SUBSTANCE");
        addiction.addResponse("Addiction is a serious struggle. Are you seeking help?");
        addiction.addResponse("What are you addicted to?");
        addiction.addResponse("How long have you been dealing with addiction?");
        addiction.addResponse("Have you tried to quit before?");
        addiction.addResponse("Do you have support for your recovery?");
        addiction.setTopic("addiction");
        addiction.setEmotionalCategory("negative");
        rules.add(addiction);

        // SELF-HARM
        Rule selfHarm = new Rule("self-harm", 95);
        selfHarm.addPattern("SELF-HARM");
        selfHarm.addPattern("SELF HARM");
        selfHarm.addPattern("CUTTING");
        selfHarm.addPattern("HURT MYSELF");
        selfHarm.addResponse("I'm concerned about you harming yourself. Please talk to a professional: call 988.");
        selfHarm.addResponse("Self-harm is a sign of deep pain. Are you seeing a therapist?");
        selfHarm.addResponse("What leads you to harm yourself?");
        selfHarm.addResponse("Have you told anyone about this?");
        selfHarm.setTopic("crisis");
        selfHarm.setEmotionalCategory("crisis");
        rules.add(selfHarm);
    }

    private void initializeCognitiveRules() {
        // THINK/THINKING
        Rule think = new Rule("think", 70);
        think.addPattern("THINK");
        think.addPattern("THINKING");
        think.addPattern("THOUGHT");
        think.addResponse("What are you thinking about?");
        think.addResponse("Tell me more about these thoughts.");
        think.addResponse("Why do you think that?");
        think.addResponse("How long have you been thinking about this?");
        think.addResponse("What do these thoughts mean to you?");
        think.setTopic("thoughts");
        rules.add(think);

        // REMEMBER/MEMORY
        Rule remember = new Rule("remember", 75);
        remember.addPattern("REMEMBER");
        remember.addPattern("MEMORY");
        remember.addPattern("MEMORIES");
        remember.addPattern("RECALL");
        remember.addResponse("What do you remember?");
        remember.addResponse("Tell me about this memory.");
        remember.addResponse("Why is this memory important to you?");
        remember.addResponse("How does this memory make you feel?");
        remember.addResponse("When did this memory occur?");
        remember.setTopic("memory");
        rules.add(remember);

        // FORGET/FORGOT
        Rule forget = new Rule("forget", 75);
        forget.addPattern("FORGET");
        forget.addPattern("FORGOT");
        forget.addPattern("FORGOTTEN");
        forget.addPattern("CAN'T REMEMBER");
        forget.addResponse("What are you having trouble remembering?");
        forget.addResponse("Is forgetting this bothering you?");
        forget.addResponse("Why do you think you can't remember?");
        forget.addResponse("How important is this memory to you?");
        forget.setTopic("memory");
        rules.add(forget);

        // DREAM/NIGHTMARE
        Rule dream = new Rule("dream", 75);
        dream.addPattern("DREAM");
        dream.addPattern("DREAMS");
        dream.addPattern("DREAMING");
        dream.addPattern("NIGHTMARE");
        dream.addResponse("Tell me about your dream.");
        dream.addResponse("What do you think this dream means?");
        dream.addResponse("How did the dream make you feel?");
        dream.addResponse("Do you often have dreams like this?");
        dream.addResponse("What symbols or themes appeared in your dream?");
        dream.setTopic("dreams");
        rules.add(dream);

        // CONFUSED/CONFUSION
        Rule confused = new Rule("confused", 80);
        confused.addPattern("CONFUSED");
        confused.addPattern("CONFUSION");
        confused.addPattern("DON'T UNDERSTAND");
        confused.addResponse("What's confusing you?");
        confused.addResponse("Tell me more about this confusion.");
        confused.addResponse("What would help clarify things for you?");
        confused.addResponse("How long have you felt confused about this?");
        confused.setTopic("confusion");
        rules.add(confused);
    }

    private void initializeFollowUpRules() {
        // Generic follow-up after discussing negative emotions
        Rule negativeFollowUp = new Rule("negative-followup", 70);
        negativeFollowUp.addCondition(ctx -> "negative".equals(ctx.getEmotionalState()));
        negativeFollowUp.addCondition(ctx -> ctx.getTurnCount() > 2);
        negativeFollowUp.addPattern("YES");
        negativeFollowUp.addPattern("NO");
        negativeFollowUp.addResponse("How are you coping with these difficult feelings?");
        negativeFollowUp.addResponse("Do you have support to help you through this?");
        negativeFollowUp.addResponse("What helps you feel better when you're struggling?");
        rules.add(negativeFollowUp);

        // Topic persistence - if same topic mentioned multiple times
        Rule topicPersistence = new Rule("topic-persistence", 75);
        topicPersistence.addCondition(ctx -> {
            String topic = ctx.getCurrentTopic();
            return topic != null && ctx.getTopicFrequency(topic) >= 3;
        });
        topicPersistence.addPattern("STILL");
        topicPersistence.addPattern("AGAIN");
        topicPersistence.addPattern("MORE");
        topicPersistence.addResponse("I notice we keep coming back to this topic. Why do you think that is?");
        topicPersistence.addResponse("This seems very important to you. What makes it so significant?");
        topicPersistence.addResponse("We've discussed this before. Has anything changed?");
        rules.add(topicPersistence);
    }

    private void initializeContextualRules() {
        // Early conversation rules (turns 1-3)
        Rule earlyConversation = new Rule("early-conversation", 60);
        earlyConversation.addCondition(ctx -> ctx.getTurnCount() <= 3);
        earlyConversation.addPattern("I");
        earlyConversation.addResponse("Thank you for sharing. What else would you like to talk about?");
        earlyConversation.addResponse("I'm here to listen. Please continue.");
        earlyConversation.addResponse("Go on, I'm listening.");
        rules.add(earlyConversation);

        // Mid conversation (turns 4-10)
        Rule midConversation = new Rule("mid-conversation", 60);
        midConversation.addCondition(ctx -> ctx.getTurnCount() > 3 && ctx.getTurnCount() <= 10);
        midConversation.addPattern("I");
        midConversation.addResponse("How does that relate to what we discussed earlier?");
        midConversation.addResponse("Is this connected to the other things you've mentioned?");
        rules.add(midConversation);

        // Long conversation check-in
        Rule longConversation = new Rule("long-conversation", 65);
        longConversation.addCondition(ctx -> ctx.getTurnCount() > 15);
        longConversation.addPattern("I");
        longConversation.addResponse("We've been talking for a while. How are you feeling about our conversation?");
        longConversation.addResponse("What insights have you gained from our discussion?");
        rules.add(longConversation);
    }

    public ConversationContext getContext() {
        return context;
    }

    // MASSIVE RULE EXPANSION - Generated programmatically for comprehensive coverage
    private void initializeExtendedEmotionalRules() {
        // FRUSTRATION
        Rule frustration = new Rule("frustration", 80);
        frustration.addPattern("FRUSTRATED");
        frustration.addPattern("FRUSTRATION");
        frustration.addPattern("ANNOYED");
        frustration.addPattern("IRRITATED");
        frustration.addResponse("What's frustrating you?");
        frustration.addResponse("Tell me more about this frustration.");
        frustration.addResponse("How do you usually deal with frustration?");
        frustration.addResponse("What would help reduce your frustration?");
        frustration.addResponse("Is this a recurring source of frustration?");
        frustration.addResponse("How is this frustration affecting you?");
        frustration.setTopic("frustration");
        frustration.setEmotionalCategory("negative");
        rules.add(frustration);

        // RESENTMENT
        Rule resentment = new Rule("resentment", 80);
        resentment.addPattern("RESENT");
        resentment.addPattern("RESENTMENT");
        resentment.addPattern("BITTER");
        resentment.addPattern("BITTERNESS");
        resentment.addResponse("Who or what do you resent?");
        resentment.addResponse("Tell me about this resentment.");
        resentment.addResponse("How long have you felt this way?");
        resentment.addResponse("What caused this resentment?");
        resentment.addResponse("How is holding onto resentment affecting you?");
        resentment.addResponse("Have you considered letting go of this resentment?");
        resentment.setTopic("resentment");
        resentment.setEmotionalCategory("negative");
        rules.add(resentment);

        // JEALOUSY
        Rule jealousy = new Rule("jealousy", 80);
        jealousy.addPattern("JEALOUS");
        jealousy.addPattern("JEALOUSY");
        jealousy.addPattern("ENVIOUS");
        jealousy.addPattern("ENVY");
        jealousy.addResponse("What are you feeling jealous about?");
        jealousy.addResponse("Tell me more about this jealousy.");
        jealousy.addResponse("Who are you jealous of?");
        jealousy.addResponse("How does this jealousy affect your relationships?");
        jealousy.addResponse("What triggers these jealous feelings?");
        jealousy.addResponse("How do you cope with jealousy?");
        jealousy.setTopic("jealousy");
        jealousy.setEmotionalCategory("negative");
        rules.add(jealousy);

        // REGRET
        Rule regret = new Rule("regret", 85);
        regret.addPattern("REGRET");
        regret.addPattern("REGRETS");
        regret.addPattern("REGRETFUL");
        regret.addResponse("What do you regret?");
        regret.addResponse("Tell me about this regret.");
        regret.addResponse("How is this regret affecting you now?");
        regret.addResponse("What would you do differently if you could?");
        regret.addResponse("Can you learn something from this regret?");
        regret.addResponse("How can you move forward despite this regret?");
        regret.setTopic("regret");
        regret.setEmotionalCategory("negative");
        rules.add(regret);

        // DISAPPOINTMENT
        Rule disappointment = new Rule("disappointment", 80);
        disappointment.addPattern("DISAPPOINTED");
        disappointment.addPattern("DISAPPOINTMENT");
        disappointment.addPattern("LET DOWN");
        disappointment.addResponse("What disappointed you?");
        disappointment.addResponse("Tell me about this disappointment.");
        disappointment.addResponse("Who or what let you down?");
        disappointment.addResponse("How are you dealing with this disappointment?");
        disappointment.addResponse("Have your expectations been unrealistic?");
        disappointment.addResponse("What can you learn from this experience?");
        disappointment.setTopic("disappointment");
        disappointment.setEmotionalCategory("negative");
        rules.add(disappointment);

        // HURT
        Rule hurt = new Rule("hurt", 85);
        hurt.addPattern("HURT");
        hurt.addPattern("HURTING");
        hurt.addPattern("PAIN");
        hurt.addPattern("PAINFUL");
        hurt.addResponse("What's hurting you?");
        hurt.addResponse("Tell me about this pain.");
        hurt.addResponse("Who hurt you?");
        hurt.addResponse("How deep is this hurt?");
        hurt.addResponse("Have you been able to express this hurt to anyone?");
        hurt.addResponse("What would help heal this hurt?");
        hurt.setTopic("hurt");
        hurt.setEmotionalCategory("negative");
        rules.add(hurt);

        // NUMBNESS/EMPTINESS
        Rule numbness = new Rule("numbness", 85);
        numbness.addPattern("NUMB");
        numbness.addPattern("NUMBNESS");
        numbness.addPattern("EMPTY");
        numbness.addPattern("EMPTINESS");
        numbness.addPattern("NOTHING");
        numbness.addResponse("Feeling numb can be a sign of depression. How long have you felt this way?");
        numbness.addResponse("What do you think is causing this emptiness?");
        numbness.addResponse("When did you start feeling numb?");
        numbness.addResponse("Is there anything that helps you feel something?");
        numbness.addResponse("Have you talked to a professional about these feelings?");
        numbness.addResponse("What used to make you feel alive?");
        numbness.setTopic("numbness");
        numbness.setEmotionalCategory("negative");
        rules.add(numbness);

        // EXCITEMENT
        Rule excitement = new Rule("excitement", 75);
        excitement.addPattern("EXCITED");
        excitement.addPattern("EXCITEMENT");
        excitement.addPattern("THRILLED");
        excitement.addPattern("ENTHUSIASTIC");
        excitement.addResponse("What are you excited about?");
        excitement.addResponse("Tell me about this excitement!");
        excitement.addResponse("How does it feel to be excited about this?");
        excitement.addResponse("What's making you feel this way?");
        excitement.addResponse("When did you start feeling excited?");
        excitement.addResponse("Share your enthusiasm with me!");
        excitement.setTopic("excitement");
        excitement.setEmotionalCategory("positive");
        rules.add(excitement);

        // GRATITUDE
        Rule gratitude = new Rule("gratitude", 75);
        gratitude.addPattern("GRATEFUL");
        gratitude.addPattern("GRATITUDE");
        gratitude.addPattern("THANKFUL");
        gratitude.addPattern("BLESSED");
        gratitude.addResponse("What are you grateful for?");
        gratitude.addResponse("Tell me about these feelings of gratitude.");
        gratitude.addResponse("How does gratitude affect your outlook?");
        gratitude.addResponse("What makes you feel blessed?");
        gratitude.addResponse("Do you express your gratitude to others?");
        gratitude.addResponse("How has practicing gratitude helped you?");
        gratitude.setTopic("gratitude");
        gratitude.setEmotionalCategory("positive");
        rules.add(gratitude);

        // PRIDE
        Rule pride = new Rule("pride", 75);
        pride.addPattern("PROUD");
        pride.addPattern("PRIDE");
        pride.addPattern("ACCOMPLISHED");
        pride.addPattern("ACHIEVEMENT");
        pride.addResponse("What are you proud of?");
        pride.addResponse("Tell me about this accomplishment!");
        pride.addResponse("How does it feel to achieve this?");
        pride.addResponse("What did you do to accomplish this?");
        pride.addResponse("Who have you shared this pride with?");
        pride.addResponse("What does this achievement mean to you?");
        pride.setTopic("pride");
        pride.setEmotionalCategory("positive");
        rules.add(pride);

        // CONFIDENCE/INSECURITY
        Rule confidence = new Rule("confidence", 80);
        confidence.addPattern("CONFIDENT");
        confidence.addPattern("CONFIDENCE");
        confidence.addPattern("INSECURE");
        confidence.addPattern("INSECURITY");
        confidence.addPattern("SELF-ESTEEM");
        confidence.addResponse("How would you describe your confidence level?");
        confidence.addResponse("What affects your self-esteem?");
        confidence.addResponse("When do you feel most confident?");
        confidence.addResponse("What makes you feel insecure?");
        confidence.addResponse("How can you build more confidence?");
        confidence.addResponse("What would boost your self-esteem?");
        confidence.setTopic("confidence");
        rules.add(confidence);

        // PESSIMISM/OPTIMISM
        Rule outlook = new Rule("outlook", 75);
        outlook.addPattern("OPTIMISTIC");
        outlook.addPattern("PESSIMISTIC");
        outlook.addPattern("POSITIVE");
        outlook.addPattern("NEGATIVE");
        outlook.addPattern("HOPEFUL");
        outlook.addPattern("HOPELESS");
        outlook.addResponse("How would you describe your outlook on life?");
        outlook.addResponse("Are you generally optimistic or pessimistic?");
        outlook.addResponse("What influences your perspective?");
        outlook.addResponse("Has your outlook changed recently?");
        outlook.addResponse("What would make you more optimistic?");
        outlook.addResponse("How does your attitude affect your life?");
        outlook.setTopic("outlook");
        rules.add(outlook);

        // REJECTION
        Rule rejection = new Rule("rejection", 85);
        rejection.addPattern("REJECTED");
        rejection.addPattern("REJECTION");
        rejection.addPattern("UNWANTED");
        rejection.addPattern("ABANDONED");
        rejection.addResponse("I'm sorry you feel rejected. What happened?");
        rejection.addResponse("Rejection can be very painful. Tell me more.");
        rejection.addResponse("Who rejected you?");
        rejection.addResponse("How are you coping with this rejection?");
        rejection.addResponse("Have you experienced rejection before?");
        rejection.addResponse("What does this rejection mean to you?");
        rejection.setTopic("rejection");
        rejection.setEmotionalCategory("negative");
        rules.add(rejection);

        // ACCEPTANCE
        Rule acceptance = new Rule("acceptance", 75);
        acceptance.addPattern("ACCEPTED");
        acceptance.addPattern("ACCEPTANCE");
        acceptance.addPattern("BELONG");
        acceptance.addPattern("WELCOMED");
        acceptance.addResponse("How does it feel to be accepted?");
        acceptance.addResponse("What does acceptance mean to you?");
        acceptance.addResponse("Who has accepted you?");
        acceptance.addResponse("How important is acceptance to you?");
        acceptance.addResponse("Do you accept yourself?");
        acceptance.addResponse("What does belonging feel like?");
        acceptance.setTopic("acceptance");
        acceptance.setEmotionalCategory("positive");
        rules.add(acceptance);
    }

    private void initializeExtendedRelationshipRules() {
        // INTIMACY
        Rule intimacy = new Rule("intimacy", 80);
        intimacy.addPattern("INTIMACY");
        intimacy.addPattern("INTIMATE");
        intimacy.addPattern("CLOSENESS");
        intimacy.addPattern("CONNECTION");
        intimacy.addResponse("Tell me about intimacy in your relationship.");
        intimacy.addResponse("What does intimacy mean to you?");
        intimacy.addResponse("How is the intimacy in your relationship?");
        intimacy.addResponse("What kind of intimacy do you crave?");
        intimacy.addResponse("Do you feel connected to your partner?");
        intimacy.addResponse("What blocks intimacy for you?");
        intimacy.setTopic("intimacy");
        rules.add(intimacy);

        // COMMUNICATION
        Rule communication = new Rule("communication", 80);
        communication.addPattern("COMMUNICATION");
        communication.addPattern("COMMUNICATE");
        communication.addPattern("TALKING");
        communication.addPattern("LISTENING");
        communication.addResponse("How is communication in your relationship?");
        communication.addResponse("Do you and your partner communicate well?");
        communication.addResponse("What communication challenges do you face?");
        communication.addResponse("How do you express yourself to your partner?");
        communication.addResponse("Does your partner listen to you?");
        communication.addResponse("What would improve your communication?");
        communication.setTopic("communication");
        rules.add(communication);

        // DISTANCE/SEPARATION
        Rule distance = new Rule("distance", 80);
        distance.addPattern("DISTANCE");
        distance.addPattern("DISTANT");
        distance.addPattern("SEPARATION");
        distance.addPattern("SEPARATED");
        distance.addPattern("APART");
        distance.addResponse("What's causing distance in your relationship?");
        distance.addResponse("How does this separation affect you?");
        distance.addResponse("Do you want to bridge this distance?");
        distance.addResponse("What created this distance?");
        distance.addResponse("How long have you felt distant?");
        distance.addResponse("What would bring you closer?");
        distance.setTopic("distance");
        rules.add(distance);

        // COMMITMENT
        Rule commitment = new Rule("commitment", 80);
        commitment.addPattern("COMMITMENT");
        commitment.addPattern("COMMITTED");
        commitment.addPattern("COMMITMENT ISSUES");
        commitment.addPattern("AFRAID TO COMMIT");
        commitment.addResponse("How do you feel about commitment?");
        commitment.addResponse("What does commitment mean to you?");
        commitment.addResponse("Are you afraid of commitment?");
        commitment.addResponse("What's your history with commitment?");
        commitment.addResponse("Is your partner committed to you?");
        commitment.addResponse("What's holding you back from committing?");
        commitment.setTopic("commitment");
        rules.add(commitment);

        // DATING
        Rule dating = new Rule("dating", 75);
        dating.addPattern("DATING");
        dating.addPattern("DATE");
        dating.addPattern("DATES");
        dating.addPattern("FIRST DATE");
        dating.addResponse("How is dating going for you?");
        dating.addResponse("What's your dating experience like?");
        dating.addResponse("What are you looking for in dating?");
        dating.addResponse("What challenges do you face in dating?");
        dating.addResponse("How do you feel about dating?");
        dating.addResponse("Tell me about your dating life.");
        dating.setTopic("dating");
        rules.add(dating);

        // SINGLE
        Rule single = new Rule("single", 75);
        single.addPattern("SINGLE");
        single.addPattern("ALONE");
        single.addPattern("NO PARTNER");
        single.addPattern("UNATTACHED");
        single.addResponse("How do you feel about being single?");
        single.addResponse("Are you happy being single?");
        single.addResponse("What's it like being single for you?");
        single.addResponse("Do you want to be in a relationship?");
        single.addResponse("How long have you been single?");
        single.addResponse("What are the benefits of being single?");
        single.setTopic("single");
        rules.add(single);

        // EX PARTNER
        Rule ex = new Rule("ex", 80);
        ex.addPattern("EX-GIRLFRIEND");
        ex.addPattern("EX-BOYFRIEND");
        ex.addPattern("EX-WIFE");
        ex.addPattern("EX-HUSBAND");
        ex.addPattern("MY EX");
        ex.addResponse("Tell me about your ex.");
        ex.addResponse("How do you feel about your ex now?");
        ex.addResponse("What ended that relationship?");
        ex.addResponse("Do you still have feelings for your ex?");
        ex.addResponse("How has that relationship affected you?");
        ex.addResponse("Have you moved on from your ex?");
        ex.setTopic("ex");
        rules.add(ex);

        // COMPATIBILITY
        Rule compatibility = new Rule("compatibility", 75);
        compatibility.addPattern("COMPATIBLE");
        compatibility.addPattern("COMPATIBILITY");
        compatibility.addPattern("RIGHT FOR EACH OTHER");
        compatibility.addPattern("MISMATCH");
        compatibility.addResponse("How compatible are you with your partner?");
        compatibility.addResponse("What makes you compatible?");
        compatibility.addResponse("Where are you incompatible?");
        compatibility.addResponse("Does compatibility matter to you?");
        compatibility.addResponse("What compatibility issues concern you?");
        compatibility.addResponse("Can differences in compatibility be overcome?");
        compatibility.setTopic("compatibility");
        rules.add(compatibility);

        // BOUNDARIES
        Rule boundaries = new Rule("boundaries", 80);
        boundaries.addPattern("BOUNDARIES");
        boundaries.addPattern("BOUNDARY");
        boundaries.addPattern("LIMITS");
        boundaries.addPattern("RESPECT");
        boundaries.addResponse("Do you have healthy boundaries?");
        boundaries.addResponse("Are your boundaries being respected?");
        boundaries.addResponse("What boundaries do you need to set?");
        boundaries.addResponse("How do you communicate your boundaries?");
        boundaries.addResponse("What happens when your boundaries are crossed?");
        boundaries.addResponse("Tell me about boundaries in your relationships.");
        boundaries.setTopic("boundaries");
        rules.add(boundaries);

        // CODEPENDENCY
        Rule codependency = new Rule("codependency", 85);
        codependency.addPattern("CODEPENDENT");
        codependency.addPattern("CODEPENDENCY");
        codependency.addPattern("TOO DEPENDENT");
        codependency.addPattern("CAN'T FUNCTION WITHOUT");
        codependency.addResponse("Do you think your relationship is codependent?");
        codependency.addResponse("How does codependency show up for you?");
        codependency.addResponse("What makes you feel dependent on your partner?");
        codependency.addResponse("Can you function independently?");
        codependency.addResponse("What would healthier boundaries look like?");
        codependency.addResponse("How did this codependency develop?");
        codependency.setTopic("codependency");
        codependency.setEmotionalCategory("negative");
        rules.add(codependency);
    }

    private void initializeExtendedFamilyRules() {
        // TOXIC FAMILY
        Rule toxicFamily = new Rule("toxic-family", 90);
        toxicFamily.addPattern("TOXIC FAMILY");
        toxicFamily.addPattern("TOXIC PARENTS");
        toxicFamily.addPattern("DYSFUNCTIONAL FAMILY");
        toxicFamily.addPattern("ABUSIVE FAMILY");
        toxicFamily.addResponse("I'm sorry you're dealing with a toxic family. How does this affect you?");
        toxicFamily.addResponse("What makes your family toxic?");
        toxicFamily.addResponse("Have you considered setting boundaries with your family?");
        toxicFamily.addResponse("How do you protect yourself from toxic family members?");
        toxicFamily.addResponse("Do you have support outside your family?");
        toxicFamily.addResponse("What would help you cope with your toxic family?");
        toxicFamily.setTopic("family");
        toxicFamily.setEmotionalCategory("negative");
        rules.add(toxicFamily);

        // ESTRANGEMENT
        Rule estrangement = new Rule("estrangement", 85);
        estrangement.addPattern("ESTRANGED");
        estrangement.addPattern("ESTRANGEMENT");
        estrangement.addPattern("NO CONTACT");
        estrangement.addPattern("CUT OFF");
        estrangement.addPattern("DISOWNED");
        estrangement.addResponse("Tell me about this estrangement.");
        estrangement.addResponse("What led to the estrangement?");
        estrangement.addResponse("How do you feel about being estranged?");
        estrangement.addResponse("Do you want to reconcile?");
        estrangement.addResponse("How has the estrangement affected you?");
        estrangement.addResponse("Was going no contact necessary?");
        estrangement.setTopic("estrangement");
        estrangement.setEmotionalCategory("negative");
        rules.add(estrangement);

        // SIBLING RIVALRY
        Rule siblingRivalry = new Rule("sibling-rivalry", 80);
        siblingRivalry.addPattern("SIBLING RIVALRY");
        siblingRivalry.addPattern("COMPETE WITH SIBLING");
        siblingRivalry.addPattern("FAVORITE CHILD");
        siblingRivalry.addPattern("COMPARED TO SIBLING");
        siblingRivalry.addResponse("Tell me about the rivalry with your sibling.");
        siblingRivalry.addResponse("How does sibling rivalry affect you?");
        siblingRivalry.addResponse("Were you compared to your sibling growing up?");
        siblingRivalry.addResponse("Do you feel like the favorite child?");
        siblingRivalry.addResponse("How has this competition affected your relationship?");
        siblingRivalry.addResponse("Can you move past this rivalry?");
        siblingRivalry.setTopic("sibling");
        rules.add(siblingRivalry);

        // PARENTING
        Rule parenting = new Rule("parenting", 80);
        parenting.addPattern("PARENTING");
        parenting.addPattern("RAISING");
        parenting.addPattern("PARENT");
        parenting.addPattern("RAISING KIDS");
        parenting.addResponse("How is parenting going for you?");
        parenting.addResponse("What challenges do you face as a parent?");
        parenting.addResponse("What's your parenting style?");
        parenting.addResponse("How do you discipline your children?");
        parenting.addResponse("What do you find rewarding about parenting?");
        parenting.addResponse("Do you need support with parenting?");
        parenting.setTopic("parenting");
        rules.add(parenting);

        // INHERITANCE/MONEY ISSUES
        Rule inheritance = new Rule("inheritance", 80);
        inheritance.addPattern("INHERITANCE");
        inheritance.addPattern("WILL");
        inheritance.addPattern("ESTATE");
        inheritance.addPattern("FAMILY MONEY");
        inheritance.addResponse("Tell me about this inheritance situation.");
        inheritance.addResponse("How is money affecting your family?");
        inheritance.addResponse("Are there disputes over inheritance?");
        inheritance.addResponse("How do financial issues impact family relationships?");
        inheritance.addResponse("What's the conflict about?");
        inheritance.addResponse("Can this be resolved fairly?");
        inheritance.setTopic("family");
        rules.add(inheritance);

        // CAREGIVING
        Rule caregiving = new Rule("caregiving", 85);
        caregiving.addPattern("CAREGIVER");
        caregiving.addPattern("CAREGIVING");
        caregiving.addPattern("TAKING CARE OF");
        caregiving.addPattern("CARING FOR");
        caregiving.addResponse("Who are you caring for?");
        caregiving.addResponse("How is caregiving affecting you?");
        caregiving.addResponse("Do you have support in your caregiving role?");
        caregiving.addResponse("Are you experiencing caregiver burnout?");
        caregiving.addResponse("What's most challenging about caregiving?");
        caregiving.addResponse("How do you take care of yourself while caregiving?");
        caregiving.setTopic("caregiving");
        rules.add(caregiving);

        // ADOPTION
        Rule adoption = new Rule("adoption", 80);
        adoption.addPattern("ADOPTED");
        adoption.addPattern("ADOPTION");
        adoption.addPattern("ADOPTIVE");
        adoption.addPattern("BIRTH PARENTS");
        adoption.addResponse("Tell me about your adoption experience.");
        adoption.addResponse("How has being adopted affected you?");
        adoption.addResponse("Do you know your birth parents?");
        adoption.addResponse("What's your relationship with your adoptive family?");
        adoption.addResponse("Have you searched for your biological family?");
        adoption.addResponse("How do you feel about your adoption?");
        adoption.setTopic("adoption");
        rules.add(adoption);
    }

    private void initializeExtendedWorkRules() {
        // COWORKER CONFLICT
        Rule coworkerConflict = new Rule("coworker-conflict", 80);
        coworkerConflict.addPattern("COWORKER");
        coworkerConflict.addPattern("COLLEAGUE");
        coworkerConflict.addPattern("TEAM MEMBER");
        coworkerConflict.addPattern("DIFFICULT COWORKER");
        coworkerConflict.addResponse("Tell me about your coworker situation.");
        coworkerConflict.addResponse("What's the issue with your coworker?");
        coworkerConflict.addResponse("How does this coworker affect your work?");
        coworkerConflict.addResponse("Have you addressed this with them directly?");
        coworkerConflict.addResponse("What would improve this work relationship?");
        coworkerConflict.addResponse("Can you avoid working with this person?");
        coworkerConflict.setTopic("work");
        rules.add(coworkerConflict);

        // PROMOTION/ADVANCEMENT
        Rule promotion = new Rule("promotion", 75);
        promotion.addPattern("PROMOTION");
        promotion.addPattern("PROMOTED");
        promotion.addPattern("ADVANCEMENT");
        promotion.addPattern("RAISE");
        promotion.addPattern("CAREER GROWTH");
        promotion.addResponse("Tell me about this promotion.");
        promotion.addResponse("How do you feel about your career advancement?");
        promotion.addResponse("What are your career goals?");
        promotion.addResponse("Are you being considered for promotion?");
        promotion.addResponse("What's holding back your advancement?");
        promotion.addResponse("How can you achieve your career goals?");
        promotion.setTopic("career");
        promotion.setEmotionalCategory("positive");
        rules.add(promotion);

        // WORKPLACE HARASSMENT
        Rule harassment = new Rule("harassment", 95);
        harassment.addPattern("HARASSMENT");
        harassment.addPattern("HARASSED");
        harassment.addPattern("HOSTILE WORK");
        harassment.addPattern("BULLYING");
        harassment.addPattern("BULLIED");
        harassment.addResponse("I'm concerned about this harassment. Have you reported it?");
        harassment.addResponse("What kind of harassment are you experiencing?");
        harassment.addResponse("Who is harassing you?");
        harassment.addResponse("Do you have HR support?");
        harassment.addResponse("Have you documented these incidents?");
        harassment.addResponse("How is this affecting your wellbeing?");
        harassment.setTopic("harassment");
        harassment.setEmotionalCategory("negative");
        rules.add(harassment);

        // IMPOSTER SYNDROME
        Rule imposter = new Rule("imposter", 85);
        imposter.addPattern("IMPOSTER SYNDROME");
        imposter.addPattern("IMPOSTER");
        imposter.addPattern("DON'T DESERVE");
        imposter.addPattern("FRAUD");
        imposter.addPattern("NOT QUALIFIED");
        imposter.addResponse("Imposter syndrome is very common. Tell me more.");
        imposter.addResponse("Why do you feel like an imposter?");
        imposter.addResponse("What makes you doubt your qualifications?");
        imposter.addResponse("Have others validated your competence?");
        imposter.addResponse("What evidence contradicts your imposter feelings?");
        imposter.addResponse("How is imposter syndrome affecting your work?");
        imposter.setTopic("imposter");
        imposter.setEmotionalCategory("negative");
        rules.add(imposter);

        // WORKLIFE BALANCE
        Rule worklife = new Rule("worklife", 80);
        worklife.addPattern("WORK-LIFE BALANCE");
        worklife.addPattern("WORK LIFE BALANCE");
        worklife.addPattern("TOO MUCH WORK");
        worklife.addPattern("NO TIME FOR LIFE");
        worklife.addPattern("ALWAYS WORKING");
        worklife.addResponse("How is your work-life balance?");
        worklife.addResponse("Do you feel like work dominates your life?");
        worklife.addResponse("What would better balance look like for you?");
        worklife.addResponse("Can you set better boundaries with work?");
        worklife.addResponse("What's preventing you from having balance?");
        worklife.addResponse("How important is work-life balance to you?");
        worklife.setTopic("balance");
        rules.add(worklife);

        // JOB SATISFACTION
        Rule jobSatisfaction = new Rule("job-satisfaction", 75);
        jobSatisfaction.addPattern("JOB SATISFACTION");
        jobSatisfaction.addPattern("SATISFYING");
        jobSatisfaction.addPattern("FULFILLING");
        jobSatisfaction.addPattern("UNFULFILLING");
        jobSatisfaction.addPattern("MEANINGLESS WORK");
        jobSatisfaction.addResponse("How satisfied are you with your job?");
        jobSatisfaction.addResponse("What would make your work more fulfilling?");
        jobSatisfaction.addResponse("Do you find meaning in your work?");
        jobSatisfaction.addResponse("What aspects of your job do you enjoy?");
        jobSatisfaction.addResponse("What would you change about your job?");
        jobSatisfaction.addResponse("Are you in the right career?");
        jobSatisfaction.setTopic("work");
        rules.add(jobSatisfaction);

        // RETIREMENT
        Rule retirement = new Rule("retirement", 75);
        retirement.addPattern("RETIREMENT");
        retirement.addPattern("RETIRING");
        retirement.addPattern("RETIRED");
        retirement.addResponse("Tell me about your retirement.");
        retirement.addResponse("How do you feel about retirement?");
        retirement.addResponse("What are your retirement plans?");
        retirement.addResponse("Are you financially ready to retire?");
        retirement.addResponse("What will you do in retirement?");
        retirement.addResponse("How is the transition to retirement?");
        retirement.setTopic("retirement");
        rules.add(retirement);
    }

    private void initializeExtendedMentalHealthRules() {
        // OCD
        Rule ocd = new Rule("ocd", 90);
        ocd.addPattern("OCD");
        ocd.addPattern("OBSESSIVE");
        ocd.addPattern("COMPULSIVE");
        ocd.addPattern("INTRUSIVE THOUGHTS");
        ocd.addPattern("RITUALS");
        ocd.addResponse("Tell me about your OCD.");
        ocd.addResponse("What obsessions or compulsions do you experience?");
        ocd.addResponse("How does OCD affect your daily life?");
        ocd.addResponse("Are you receiving treatment for OCD?");
        ocd.addResponse("What triggers your OCD symptoms?");
        ocd.addResponse("Have you tried exposure therapy?");
        ocd.setTopic("ocd");
        ocd.setEmotionalCategory("negative");
        rules.add(ocd);

        // BIPOLAR
        Rule bipolar = new Rule("bipolar", 90);
        bipolar.addPattern("BIPOLAR");
        bipolar.addPattern("MANIC");
        bipolar.addPattern("MANIA");
        bipolar.addPattern("MOOD SWINGS");
        bipolar.addPattern("DEPRESSIVE EPISODE");
        bipolar.addResponse("Tell me about your bipolar disorder.");
        bipolar.addResponse("Are you experiencing mania or depression right now?");
        bipolar.addResponse("How are you managing your bipolar disorder?");
        bipolar.addResponse("Are you taking mood stabilizers?");
        bipolar.addResponse("What triggers your mood episodes?");
        bipolar.addResponse("Do you have a psychiatrist?");
        bipolar.setTopic("bipolar");
        bipolar.setEmotionalCategory("negative");
        rules.add(bipolar);

        // EATING DISORDERS
        Rule eatingDisorder = new Rule("eating-disorder", 95);
        eatingDisorder.addPattern("ANOREXIA");
        eatingDisorder.addPattern("BULIMIA");
        eatingDisorder.addPattern("BINGE EATING");
        eatingDisorder.addPattern("EATING DISORDER");
        eatingDisorder.addPattern("PURGING");
        eatingDisorder.addResponse("I'm concerned about this eating disorder. Are you getting help?");
        eatingDisorder.addResponse("Tell me about your relationship with food.");
        eatingDisorder.addResponse("How long have you struggled with this?");
        eatingDisorder.addResponse("Do you have a treatment team?");
        eatingDisorder.addResponse("What triggers these behaviors?");
        eatingDisorder.addResponse("How is your physical health?");
        eatingDisorder.setTopic("eating-disorder");
        eatingDisorder.setEmotionalCategory("crisis");
        rules.add(eatingDisorder);

        // INSOMNIA/SLEEP
        Rule insomnia = new Rule("insomnia", 80);
        insomnia.addPattern("INSOMNIA");
        insomnia.addPattern("CAN'T SLEEP");
        insomnia.addPattern("SLEEP PROBLEMS");
        insomnia.addPattern("NOT SLEEPING");
        insomnia.addPattern("TIRED");
        insomnia.addResponse("How long have you been having sleep problems?");
        insomnia.addResponse("What do you think is causing your insomnia?");
        insomnia.addResponse("Have you tried sleep hygiene practices?");
        insomnia.addResponse("How is lack of sleep affecting you?");
        insomnia.addResponse("Are you using any sleep aids?");
        insomnia.addResponse("When was the last time you slept well?");
        insomnia.setTopic("sleep");
        insomnia.setEmotionalCategory("negative");
        rules.add(insomnia);

        // SOCIAL ANXIETY
        Rule socialAnxiety = new Rule("social-anxiety", 85);
        socialAnxiety.addPattern("SOCIAL ANXIETY");
        socialAnxiety.addPattern("AFRAID OF PEOPLE");
        socialAnxiety.addPattern("SHY");
        socialAnxiety.addPattern("AWKWARD");
        socialAnxiety.addPattern("SOCIAL SITUATIONS");
        socialAnxiety.addResponse("Tell me about your social anxiety.");
        socialAnxiety.addResponse("What social situations trigger your anxiety?");
        socialAnxiety.addResponse("How does social anxiety affect your life?");
        socialAnxiety.addResponse("Have you tried exposure to social situations?");
        socialAnxiety.addResponse("What helps reduce your social anxiety?");
        socialAnxiety.addResponse("Do you avoid social situations?");
        socialAnxiety.setTopic("social-anxiety");
        socialAnxiety.setEmotionalCategory("negative");
        rules.add(socialAnxiety);

        // RECOVERY
        Rule recovery = new Rule("recovery", 85);
        recovery.addPattern("RECOVERY");
        recovery.addPattern("RECOVERING");
        recovery.addPattern("SOBER");
        recovery.addPattern("SOBRIETY");
        recovery.addPattern("CLEAN");
        recovery.addResponse("How is your recovery going?");
        recovery.addResponse("Tell me about your recovery journey.");
        recovery.addResponse("What helps you stay in recovery?");
        recovery.addResponse("How long have you been in recovery?");
        recovery.addResponse("Do you attend support meetings?");
        recovery.addResponse("What challenges are you facing in recovery?");
        recovery.setTopic("recovery");
        recovery.setEmotionalCategory("positive");
        rules.add(recovery);

        // RELAPSE
        Rule relapse = new Rule("relapse", 90);
        relapse.addPattern("RELAPSE");
        relapse.addPattern("RELAPSED");
        relapse.addPattern("FELL OFF THE WAGON");
        relapse.addPattern("USING AGAIN");
        relapse.addResponse("I'm sorry you relapsed. What happened?");
        relapse.addResponse("Relapse is part of recovery for many people. How can you get back on track?");
        relapse.addResponse("What triggered the relapse?");
        relapse.addResponse("Have you reached out for support?");
        relapse.addResponse("What did you learn from this relapse?");
        relapse.addResponse("Are you ready to try again?");
        relapse.setTopic("relapse");
        relapse.setEmotionalCategory("negative");
        rules.add(relapse);

        // DISSOCIATION
        Rule dissociation = new Rule("dissociation", 90);
        dissociation.addPattern("DISSOCIATION");
        dissociation.addPattern("DISSOCIATING");
        dissociation.addPattern("OUT OF BODY");
        dissociation.addPattern("DETACHED");
        dissociation.addPattern("DEREALIZATION");
        dissociation.addResponse("Tell me about these dissociative experiences.");
        dissociation.addResponse("When do you dissociate?");
        dissociation.addResponse("How often does this happen?");
        dissociation.addResponse("What triggers dissociation for you?");
        dissociation.addResponse("Are you working with a trauma specialist?");
        dissociation.addResponse("What helps you feel grounded?");
        dissociation.setTopic("dissociation");
        dissociation.setEmotionalCategory("negative");
        rules.add(dissociation);
    }
}

