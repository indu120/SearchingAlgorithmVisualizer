package com.teachingaid.mcq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Question bank containing 50+ MCQ questions about searching algorithms
 * Questions are categorized by difficulty levels and algorithm types
 */
public class MCQQuestionBank {
    private static final List<MCQQuestion> questions = new ArrayList<>();

    static {
        initializeQuestions();
    }

    private static void initializeQuestions() {
        // LEVEL 1 - Easy Questions (10 points each)
        
        // Linear Search - Easy
        questions.add(new MCQQuestion(
            "What is the time complexity of linear search in the worst case?",
            new String[]{"O(1)", "O(log n)", "O(n)", "O(n²)"},
            2, 1, "Linear Search",
            "Linear search checks each element sequentially, so in worst case it checks all n elements.",
            10
        ));

        questions.add(new MCQQuestion(
            "Linear search works on:",
            new String[]{"Only sorted arrays", "Only unsorted arrays", "Both sorted and unsorted arrays", "Only arrays with unique elements"},
            2, 1, "Linear Search",
            "Linear search doesn't require any specific order, it works on both sorted and unsorted arrays.",
            10
        ));

        questions.add(new MCQQuestion(
            "What is the space complexity of linear search?",
            new String[]{"O(1)", "O(log n)", "O(n)", "O(n²)"},
            0, 1, "Linear Search",
            "Linear search uses only a constant amount of extra space for the loop variable.",
            10
        ));

        // Binary Search - Easy
        questions.add(new MCQQuestion(
            "Binary search requires the array to be:",
            new String[]{"Unsorted", "Sorted", "Contains duplicates", "Of even length"},
            1, 1, "Binary Search",
            "Binary search works by comparing the middle element and eliminating half the search space, requiring sorted data.",
            10
        ));

        questions.add(new MCQQuestion(
            "What is the time complexity of binary search?",
            new String[]{"O(n)", "O(log n)", "O(n log n)", "O(1)"},
            1, 1, "Binary Search",
            "Binary search divides the search space in half each time, resulting in O(log n) complexity.",
            10
        ));

        questions.add(new MCQQuestion(
            "In binary search, if the target is smaller than the middle element, we:",
            new String[]{"Search the right half", "Search the left half", "Search both halves", "Target not found"},
            1, 1, "Binary Search",
            "Since the array is sorted, if target < middle, it must be in the left half.",
            10
        ));

        // General Concepts - Easy
        questions.add(new MCQQuestion(
            "Which algorithm is generally faster for large sorted datasets?",
            new String[]{"Linear Search", "Binary Search", "Both are same", "Depends on data type"},
            1, 1, "General",
            "Binary search is O(log n) while linear search is O(n), so binary search is faster for large datasets.",
            10
        ));

        questions.add(new MCQQuestion(
            "What does 'Big O notation' represent?",
            new String[]{"Exact running time", "Space used", "Worst-case time complexity", "Best-case time complexity"},
            2, 1, "General",
            "Big O notation describes the upper bound or worst-case time complexity of an algorithm.",
            10
        ));

        questions.add(new MCQQuestion(
            "Which of the following is NOT a searching algorithm?",
            new String[]{"Linear Search", "Binary Search", "Quick Sort", "Jump Search"},
            2, 1, "General",
            "Quick Sort is a sorting algorithm, not a searching algorithm.",
            10
        ));

        questions.add(new MCQQuestion(
            "In an array of 100 elements, what is the maximum number of comparisons needed by linear search?",
            new String[]{"50", "100", "10", "1"},
            1, 1, "Linear Search",
            "In worst case, linear search checks all elements, so maximum 100 comparisons.",
            10
        ));

        // LEVEL 2 - Medium Questions (15 points each)

        questions.add(new MCQQuestion(
            "What is the time complexity of Jump Search?",
            new String[]{"O(n)", "O(log n)", "O(√n)", "O(n²)"},
            2, 2, "Jump Search",
            "Jump search jumps √n steps and then does linear search, resulting in O(√n) complexity.",
            15
        ));

        questions.add(new MCQQuestion(
            "What is the optimal jump size for Jump Search in an array of size n?",
            new String[]{"n/2", "√n", "log n", "n/4"},
            1, 2, "Jump Search",
            "The optimal jump size is √n, which minimizes the total number of comparisons.",
            15
        ));

        questions.add(new MCQQuestion(
            "Exponential Search is also known as:",
            new String[]{"Galloping Search", "Doubling Search", "Both A and B", "Neither A nor B"},
            2, 2, "Exponential Search",
            "Exponential Search is also called Galloping Search or Doubling Search.",
            15
        ));

        questions.add(new MCQQuestion(
            "In binary search, for an array of 16 elements, maximum comparisons needed are:",
            new String[]{"16", "8", "4", "5"},
            3, 2, "Binary Search",
            "Maximum comparisons = ⌊log₂(16)⌋ + 1 = 4 + 1 = 5",
            15
        ));

        questions.add(new MCQQuestion(
            "Which search algorithm performs better when the target is located near the beginning of a large sorted array?",
            new String[]{"Binary Search", "Linear Search", "Exponential Search", "Jump Search"},
            2, 2, "Exponential Search",
            "Exponential search starts with small bounds and doubles them, making it efficient for targets near the beginning.",
            15
        ));

        questions.add(new MCQQuestion(
            "What happens if we apply binary search on an unsorted array?",
            new String[]{"It works correctly", "It may give incorrect results", "It throws an error", "It sorts the array first"},
            1, 2, "Binary Search",
            "Binary search assumes sorted data. On unsorted arrays, it may miss the target even if it exists.",
            15
        ));

        questions.add(new MCQQuestion(
            "In Jump Search, after finding the block, which algorithm is used to search within that block?",
            new String[]{"Binary Search", "Linear Search", "Exponential Search", "Interpolation Search"},
            1, 2, "Jump Search",
            "After finding the correct block in Jump Search, linear search is used within that block.",
            15
        ));

        questions.add(new MCQQuestion(
            "Which string matching algorithm has the best average-case time complexity?",
            new String[]{"Naive Algorithm", "KMP Algorithm", "Boyer-Moore Algorithm", "All are same"},
            2, 2, "String Search",
            "Boyer-Moore algorithm has O(n/m) average case complexity, which is better than others.",
            15
        ));

        questions.add(new MCQQuestion(
            "What is the space complexity of KMP string matching algorithm?",
            new String[]{"O(1)", "O(m)", "O(n)", "O(n+m)"},
            1, 2, "String Search",
            "KMP algorithm uses O(m) extra space to store the failure function array, where m is pattern length.",
            15
        ));

        questions.add(new MCQQuestion(
            "In the worst case, how many elements does Jump Search examine?",
            new String[]{"√n + √n", "2√n", "√n + (√n - 1)", "All of the above are correct"},
            3, 2, "Jump Search",
            "Jump Search examines at most √n elements during jumping phase and (√n - 1) in linear search phase.",
            15
        ));

        // LEVEL 3 - Hard Questions (20 points each)

        questions.add(new MCQQuestion(
            "What is the recurrence relation for binary search?",
            new String[]{"T(n) = T(n/2) + O(1)", "T(n) = 2T(n/2) + O(1)", "T(n) = T(n-1) + O(1)", "T(n) = T(n/2) + O(n)"},
            0, 3, "Binary Search",
            "Binary search processes one half and does constant work, so T(n) = T(n/2) + O(1).",
            20
        ));

        questions.add(new MCQQuestion(
            "In Exponential Search, what is the time complexity of finding the range?",
            new String[]{"O(log i)", "O(i)", "O(log n)", "O(√n)"},
            0, 3, "Exponential Search",
            "Finding range takes O(log i) time where i is the position of target element.",
            20
        ));

        questions.add(new MCQQuestion(
            "Which statement about Boyer-Moore algorithm is correct?",
            new String[]{"It always scans left to right", "It can skip characters during matching", "It has O(nm) worst case", "Both B and C are correct"},
            3, 3, "String Search",
            "Boyer-Moore can skip characters and has O(nm) worst-case complexity, though average case is much better.",
            20
        ));

        questions.add(new MCQQuestion(
            "What is the key advantage of Interpolation Search over Binary Search?",
            new String[]{"Works on unsorted data", "Better for uniformly distributed data", "Simpler implementation", "Uses less memory"},
            1, 3, "Advanced Search",
            "Interpolation Search performs better than Binary Search when data is uniformly distributed.",
            20
        ));

        questions.add(new MCQQuestion(
            "In KMP algorithm, what does the failure function represent?",
            new String[]{"Number of failures", "Longest proper prefix-suffix match", "Pattern mismatches", "Text position"},
            1, 3, "String Search",
            "The failure function stores the length of the longest proper prefix which is also a suffix.",
            20
        ));

        questions.add(new MCQQuestion(
            "For a sorted array with n elements, if we know the target is in the first √n elements, which algorithm is most efficient?",
            new String[]{"Binary Search", "Linear Search", "Exponential Search", "Jump Search"},
            2, 3, "Advanced Search",
            "Exponential Search would find the range quickly and then use binary search on the small range.",
            20
        ));

        questions.add(new MCQQuestion(
            "What is the worst-case time complexity of naive string matching algorithm?",
            new String[]{"O(n)", "O(m)", "O(nm)", "O(n+m)"},
            2, 3, "String Search",
            "Naive algorithm may compare each of n-m+1 positions with m characters, giving O(nm) complexity.",
            20
        ));

        questions.add(new MCQQuestion(
            "In binary search, the condition for the loop termination is usually:",
            new String[]{"left < right", "left <= right", "left > right", "Both A and B can work"},
            3, 3, "Binary Search",
            "Both left < right and left <= right can work depending on implementation details.",
            20
        ));

        questions.add(new MCQQuestion(
            "Which algorithm would be best for searching in a sorted linked list?",
            new String[]{"Binary Search", "Jump Search", "Linear Search", "Exponential Search"},
            2, 3, "General",
            "Linked lists don't provide random access, so only linear search can be efficiently implemented.",
            20
        ));

        questions.add(new MCQQuestion(
            "What is the minimum number of comparisons needed to find an element using binary search in a sorted array of 1000 elements?",
            new String[]{"1", "10", "500", "1000"},
            0, 3, "Binary Search",
            "Best case is when element is found at first comparison (middle element), so minimum is 1.",
            20
        ));

        // LEVEL 4 - Expert Questions (25 points each)

        questions.add(new MCQQuestion(
            "In the context of cache performance, which search algorithm typically has better spatial locality?",
            new String[]{"Binary Search", "Linear Search", "Jump Search", "Exponential Search"},
            1, 4, "Advanced Concepts",
            "Linear search accesses consecutive memory locations, providing better cache spatial locality.",
            25
        ));

        questions.add(new MCQQuestion(
            "What is the optimal value of k in k-ary search (generalization of binary search)?",
            new String[]{"2", "3", "⌈log₂ n⌉", "It depends on the cost of comparison vs memory access"},
            3, 4, "Advanced Search",
            "Optimal k depends on relative costs of comparisons and memory accesses in the specific system.",
            25
        ));

        questions.add(new MCQQuestion(
            "In external sorting/searching, which factor becomes most critical?",
            new String[]{"CPU speed", "Memory size", "Disk I/O operations", "Algorithm complexity"},
            2, 4, "Advanced Concepts",
            "For external data, minimizing disk I/O operations becomes more critical than algorithmic complexity.",
            25
        ));

        questions.add(new MCQQuestion(
            "Which string matching algorithm is most suitable for multiple pattern searching?",
            new String[]{"KMP", "Boyer-Moore", "Aho-Corasick", "Naive"},
            2, 4, "String Search",
            "Aho-Corasick algorithm is specifically designed for finding multiple patterns simultaneously.",
            25
        ));

        questions.add(new MCQQuestion(
            "What is the expected number of probes in binary search for a successful search?",
            new String[]{"log₂ n", "log₂ n - 1", "log₂ n + 1", "(log₂ n) - 2 + (2/n)"},
            3, 4, "Binary Search",
            "The expected number of probes in binary search is approximately (log₂ n) - 2 + (2/n).",
            25
        ));

        questions.add(new MCQQuestion(
            "In probabilistic analysis, if each element has equal probability of being searched, what is the average-case complexity of linear search?",
            new String[]{"O(n)", "O(n/2)", "O(log n)", "O(1)"},
            1, 4, "Linear Search",
            "On average, we expect to find the element after checking n/2 elements, so average case is O(n/2) = O(n).",
            25
        ));

        questions.add(new MCQQuestion(
            "Which search technique is most appropriate for searching in a B+ tree?",
            new String[]{"Linear Search", "Binary Search within nodes", "Hash-based search", "Depth-first search"},
            1, 4, "Advanced Data Structures",
            "B+ trees use binary search within each node to efficiently locate keys.",
            25
        ));

        questions.add(new MCQQuestion(
            "What is the primary disadvantage of interpolation search compared to binary search?",
            new String[]{"Higher time complexity", "Requires more memory", "Performance depends on data distribution", "Cannot handle duplicate values"},
            2, 4, "Advanced Search",
            "Interpolation search performance degrades to O(n) if data is not uniformly distributed.",
            25
        ));

        questions.add(new MCQQuestion(
            "In the context of string matching, what does the 'critical factorization' refer to?",
            new String[]{"Pattern decomposition in KMP", "Suffix-prefix matching", "Period computation in advanced algorithms", "Bad character heuristic"},
            2, 4, "String Search",
            "Critical factorization is related to period computation in advanced string matching algorithms.",
            25
        ));

        questions.add(new MCQQuestion(
            "For very large datasets that don't fit in main memory, which approach is most effective?",
            new String[]{"Use faster algorithms", "External searching with B-trees", "Compress the data first", "Use parallel processing"},
            1, 4, "Advanced Concepts",
            "External searching using B-trees minimizes disk I/O operations for large datasets.",
            25
        ));

        // LEVEL 5 - Master Questions (30 points each)

        questions.add(new MCQQuestion(
            "In the randomized binary search, what is the probability that the algorithm finds the element in exactly k comparisons?",
            new String[]{"1/2^k", "1/k", "(k-1)/2^k", "Depends on element position"},
            0, 5, "Randomized Algorithms",
            "In randomized binary search, probability of exactly k comparisons is 1/2^k for successful search.",
            30
        ));

        questions.add(new MCQQuestion(
            "What is the optimal competitive ratio for online search algorithms in the comparison model?",
            new String[]{"2", "log n", "√n", "There is no optimal ratio"},
            0, 5, "Online Algorithms",
            "The optimal competitive ratio for online search algorithms is 2 in the comparison model.",
            30
        ));

        questions.add(new MCQQuestion(
            "In the context of information theory, what is the minimum number of comparisons needed to search in a sorted array of n elements?",
            new String[]{"log₂ n", "⌈log₂ n⌉", "⌊log₂ n⌋", "⌈log₂(n+1)⌉"},
            3, 5, "Information Theory",
            "Information theoretically, we need ⌈log₂(n+1)⌉ comparisons to distinguish between n+1 outcomes.",
            30
        ));

        questions.add(new MCQQuestion(
            "Which statement about the Boyer-Moore algorithm's bad character heuristic is correct?",
            new String[]{"It always improves performance", "It can cause negative shifts", "It requires O(σ) preprocessing time", "All of the above"},
            3, 5, "String Search",
            "Bad character heuristic can cause negative shifts, requires O(σ) time, but doesn't always improve performance.",
            30
        ));

        questions.add(new MCQQuestion(
            "In the analysis of algorithms, what does the 'smoothed analysis' provide that worst-case analysis doesn't?",
            new String[]{"Better average case", "More realistic performance bounds", "Lower complexity", "Exact running time"},
            1, 5, "Algorithm Analysis",
            "Smoothed analysis provides more realistic performance bounds by considering slight perturbations of worst-case inputs.",
            30
        ));

        questions.add(new MCQQuestion(
            "What is the relationship between the number of elements examined by exponential search and the position of the target element?",
            new String[]{"Linear", "Logarithmic", "Square root", "Exponential"},
            1, 5, "Exponential Search",
            "Exponential search examines O(log i) elements where i is the position of the target element.",
            30
        ));

        questions.add(new MCQQuestion(
            "In the context of cache-oblivious algorithms, which property is most important for search algorithms?",
            new String[]{"Time complexity", "Space complexity", "Memory access patterns", "Code simplicity"},
            2, 5, "Cache-Oblivious Algorithms",
            "Cache-oblivious algorithms focus on memory access patterns to work well across different cache hierarchies.",
            30
        ));

        questions.add(new MCQQuestion(
            "What is the main theoretical limitation of comparison-based searching algorithms?",
            new String[]{"They cannot achieve O(1) time", "They are bounded by information theory", "They require sorted data", "They use too much memory"},
            1, 5, "Theoretical Limits",
            "Comparison-based algorithms are fundamentally limited by information-theoretic bounds on the number of comparisons needed.",
            30
        ));

        questions.add(new MCQQuestion(
            "In parallel binary search on p processors, what is the optimal time complexity achievable?",
            new String[]{"O(log n)", "O(log n / log p)", "O(log n / p)", "O(1)"},
            1, 5, "Parallel Algorithms",
            "Parallel binary search can achieve O(log n / log p) time complexity on p processors.",
            30
        ));

        questions.add(new MCQQuestion(
            "What is the space-time tradeoff in van Emde Boas trees for successor queries?",
            new String[]{"O(n) space, O(log n) time", "O(n log log n) space, O(log log n) time", "O(n²) space, O(1) time", "O(n) space, O(1) time"},
            1, 5, "Advanced Data Structures",
            "van Emde Boas trees use O(n log log n) space to achieve O(log log n) time for successor queries.",
            30
        ));
    }

    public static List<MCQQuestion> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public static List<MCQQuestion> getQuestionsByDifficulty(int difficulty) {
        return questions.stream()
                .filter(q -> q.getDifficulty() == difficulty)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static List<MCQQuestion> getQuestionsByCategory(String category) {
        return questions.stream()
                .filter(q -> q.getCategory().equalsIgnoreCase(category))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static int getTotalQuestions() {
        return questions.size();
    }
}
