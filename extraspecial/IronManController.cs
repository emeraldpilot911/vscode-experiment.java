using UnityEngine;

[RequireComponent(typeof(CharacterController))]
public class IronManController : MonoBehaviour
{
    [Header("Movement Settings")]
    public float moveSpeed = 8f;
    public float flightSpeed = 18f;
    public float rotationSpeed = 12f;
    public bool isFlying;

    [Header("Combat Settings")]
    public Transform repulsorMuzzleRight;
    public Transform repulsorMuzzleLeft;
    public GameObject repulsorBeamPrefab;
    public GameObject chargedBeamPrefab;
    public float chargeTimeThreshold = 1f;

    [Header("Stats")]
    public float maxSuitPower = 100f;
    public float currentSuitPower = 100f;

    private const float Gravity = -9.81f;
    private const int MaxComboStep = 3;

    private CharacterController controller;
    private Animator anim;
    private Camera mainCamera;
    private int comboStep;
    private float lastAttackTime;
    private const float ComboResetDelay = 1.2f;
    private float shootHoldTimer;
    private float verticalVelocity;
    private bool isCharging;

    private void Awake()
    {
        controller = GetComponent<CharacterController>();
        anim = GetComponent<Animator>();
        mainCamera = Camera.main;

        currentSuitPower = Mathf.Clamp(currentSuitPower, 0f, maxSuitPower);
    }

    private void Start()
    {
        Cursor.lockState = CursorLockMode.Locked;
    }

    private void Update()
    {
        HandleMovement();
        HandleFlightToggle();
        HandleMeleeCombos();
        HandleRepulsors();
        ResetComboIfIdle();
    }

    private void HandleMovement()
    {
        float horizontal = Input.GetAxis("Horizontal");
        float vertical = Input.GetAxis("Vertical");
        Vector3 moveInput = new Vector3(horizontal, 0f, vertical);
        float inputMagnitude = Mathf.Clamp01(moveInput.magnitude);

        if (inputMagnitude >= 0.1f)
        {
            moveInput.Normalize();
            float cameraYaw = mainCamera != null
                ? mainCamera.transform.eulerAngles.y
                : transform.eulerAngles.y;
            float targetAngle = Mathf.Atan2(moveInput.x, moveInput.z) * Mathf.Rad2Deg + cameraYaw;
            Vector3 moveDirection = Quaternion.Euler(0f, targetAngle, 0f) * Vector3.forward;

            Quaternion targetRotation = Quaternion.LookRotation(moveDirection);
            transform.rotation = Quaternion.Slerp(
                transform.rotation,
                targetRotation,
                Time.deltaTime * rotationSpeed
            );

            float activeSpeed = isFlying ? flightSpeed : moveSpeed;
            controller.Move(moveDirection * activeSpeed * Time.deltaTime);
        }

        if (anim != null)
        {
            anim.SetFloat("Speed", inputMagnitude);
        }

        if (isFlying)
        {
            verticalVelocity = 0f;
            float verticalFly = (Input.GetKey(KeyCode.E) ? 1f : 0f)
                - (Input.GetKey(KeyCode.Q) ? 1f : 0f);
            controller.Move(Vector3.up * verticalFly * flightSpeed * Time.deltaTime);
        }
        else
        {
            if (controller.isGrounded && verticalVelocity < 0f)
            {
                verticalVelocity = -2f;
            }

            verticalVelocity += Gravity * Time.deltaTime;
            controller.Move(Vector3.up * verticalVelocity * Time.deltaTime);
        }
    }

    private void HandleFlightToggle()
    {
        if (!Input.GetKeyDown(KeyCode.Space))
        {
            return;
        }

        isFlying = !isFlying;
        verticalVelocity = 0f;
        if (anim != null)
        {
            anim.SetBool("IsFlying", isFlying);
        }
    }

    private void HandleMeleeCombos()
    {
        if (Input.GetMouseButtonDown(0))
        {
            lastAttackTime = Time.time;
            comboStep = comboStep % MaxComboStep + 1;
            if (anim != null)
            {
                anim.SetTrigger("Melee" + comboStep);
            }
        }

        if (Input.GetKeyDown(KeyCode.E) && !isFlying && anim != null)
        {
            anim.SetTrigger("HeavyPound");
        }
    }

    private void HandleRepulsors()
    {
        if (Input.GetMouseButton(1))
        {
            shootHoldTimer += Time.deltaTime;
            if (shootHoldTimer >= chargeTimeThreshold)
            {
                isCharging = true;
            }
        }

        if (!Input.GetMouseButtonUp(1))
        {
            return;
        }

        if (isCharging)
        {
            FireChargedRepulsor();
        }
        else
        {
            FireQuickRepulsor();
        }

        shootHoldTimer = 0f;
        isCharging = false;
    }

    private void FireQuickRepulsor()
    {
        if (anim != null)
        {
            anim.SetTrigger("FireRepulsorRight");
        }

        Transform muzzle = comboStep % 2 == 0 ? repulsorMuzzleRight : repulsorMuzzleLeft;
        if (muzzle == null)
        {
            muzzle = repulsorMuzzleRight != null ? repulsorMuzzleRight : repulsorMuzzleLeft;
        }

        if (muzzle == null || repulsorBeamPrefab == null)
        {
            Debug.LogWarning("Assign a repulsor beam prefab and at least one muzzle transform.", this);
            return;
        }

        Instantiate(repulsorBeamPrefab, muzzle.position, transform.rotation);
    }

    private void FireChargedRepulsor()
    {
        if (anim != null)
        {
            anim.SetTrigger("FireUnibeam");
        }

        Transform muzzle = repulsorMuzzleRight != null
            ? repulsorMuzzleRight
            : repulsorMuzzleLeft;

        if (muzzle == null || chargedBeamPrefab == null)
        {
            Debug.LogWarning("Assign a charged beam prefab and at least one muzzle transform.", this);
            return;
        }

        Instantiate(chargedBeamPrefab, muzzle.position, transform.rotation);
    }

    private void ResetComboIfIdle()
    {
        if (comboStep > 0 && Time.time - lastAttackTime > ComboResetDelay)
        {
            comboStep = 0;
        }
    }
}
